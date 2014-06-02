<?php
require_once("dto/request_dto.php");
class SkylandModel extends Model {
	
	public function __construct() {
		parent::__construct();
	}
	
	/*
	 * resources($userid) : Returns a ResourcesDTO containing the amount
	 * 						of resources in the users possession
	 * 		$userid : The ID of the user whose resources are required
	 */
	public function resources($userid) {
		$resources = new ResourcesDTO();
		$this->database->query("SELECT * FROM user_resources
								WHERE user_id = :userid", 
								array(':userid' => $userid));
		if ($row = $this->database->fetch()) {
			$resources->gold = $row['gold'];
			$resources->whitecloud_essence = $row['whitecloud_essence'];
			$resources->thundercloud_essence = $row['thundercloud_essence'];
		}
		return $resources;
	}
	
	/*
	 * buildings($userid) : Returns an array of BuildingDTOs containing all buildings and
	 * 						their respective IDs, current levels, max levels, current progress
	 * 						and its end time if any and the next level cost required for upgrade
	 * 		$userid : The ID of the user whose buildings are required
	 */
	public function buildings($userid) {
		$buildings = array();
		$this->database->query("SELECT building_id, current_level FROM user_buildings
								WHERE user_id = :userid",
								array(':userid' => $userid));
		while ($row = $this->database->fetch()) {
			$building = new BuildingDTO();
			$building->id = $row['building_id'];
			$building->currentLevel = $row['current_level'];
			$buildings[] = $building;
		}
		foreach($buildings as $building) {
			$building->maxLevel = $this->getMaxLevel($building->id);
			$building_progress = $this->getProgress($userid, $building->id);
			$building->inProgress = $building_progress['inProgress'];
			$building->endTime = $building_progress['endTime'];
			$building->nextLevelEssence = $this->getNextLevelEssence($building->id, $building->currentLevel + 1);		
		}
		return $buildings;
	}
	
	/*
	 * getMaxLevel($buildingid) : Returns the maximum level a building can be upgraded to
	 * 		$buildingid : The ID of the targeted building
	 */
	private function getMaxLevel($buildingid) {
		$this->database->query("SELECT MAX(level) 
								AS level FROM building_prices
								WHERE building_id = :buildingid",
								array(':buildingid' => $buildingid));
		if ($row = $this->database->fetch()) {
			return $row['level'];
		}
		return null;
	}
	
	/*
	 * getProgress($userid, $buildingid) : Returns the status of a certain building
	 * 		$userid : The ID of the user whose buildings info is required
	 * 		$buildingid : The ID of the building whose info is required
	 * 		$result["inProgress"] : The progress status (true => in progress, false => not in progress)
	 * 		$result["endTime"] : The time until completion (only available if the building is in progress)*/
	private function getProgress($userid, $buildingid) {
		$this->database->query("SELECT level, enddate FROM user_building_progress
								WHERE building_id = :buildingid AND user_id = :userid",
								array(':buildingid' => $buildingid, ':userid' => $userid));
		$result = array("inProgress" => false, "endTime" => null);
		if ($row = $this->database->fetch()) {
			$result["inProgress"] = true;
			$result["endTime"] = $row['enddate'];
		}
		return $result;
	}
	
	/*
	 * getNextLevelEssence($buildingid, $level) : Returns the amount of whitecloud essence required
	 * 											  to upgrade a certain building to the next level
	 * 		$buildingid : The ID of the building
	 * 		$level : The level that the building will be upgraded to(current level + 1)
	 */
	private function getNextLevelEssence($buildingid, $level) {
		$this->database->query("SELECT * FROM building_prices
								WHERE building_id = :buildingid AND level = :level",
								array(':buildingid' => $buildingid, ':level' => $level));
		$resources = new ResourcesDTO();
		if ($row = $this->database->fetch()) {
			$resources->whitecloud_essence = $row['whitecloud_essence'];
		}
		return $resources->whitecloud_essence;
	}
	
	/*
	 * update($userid) : Updates building progress and resource gain
	 * 		$userid : The ID of the user whose info is being updated
	 */
	public function update($userid) {
		$buildings = $this->getBuildingsInProgress($userid);
		foreach ($buildings as $building) {
			$this->database->query("UPDATE user_buildings SET
									current_level = (SELECT level FROM user_building_progress WHERE id = :id)
									WHERE user_id = :userid AND building_id = :buildingid",
									array(':id' => $building['id'], ':userid' => $userid, ':buildingid' => $building['building_id']));
			$this->database->query("DELETE FROM user_building_progress WHERE id = :id",
									array(':id' => $building['id']));
		}
		$this->updateResources($userid);
	}
	
	/*
	 * updateResources($userid) : Updates the resource gain and distributes the amounted resource
	 * 		$userid : The ID of the user whose resources are being updated
	 * 		$quarryLevel : The level of the CloudQuarry - affecting the whitecloud $ thundercloud essence gain
	 * 		$villageLevel : The level of the Village - affecting the population growth
	 * 		$harborLevel : The level of the Sky Harbor - affecting the gold income
	 * 		$period : The time amounted from previous update until now
	 *		$wrm : Whitecloud research modifier
	 * 		$trm : Thundercloud research modifier
	 * 		$grm : Gold research modifier
	 */
	private function updateResources($userid) {
		$quarryLevel = $this->getCurrentLevel($userid, 2);
		$villageLevel = $this->getCurrentLevel($userid, 4);
		$harborLevel = $this->getCurrentLevel($userid, 5);
		$wrm = $this->getResearchModifier($userid, 0);
		$trm = $this->getResearchModifier($userid, 1);
		$grm = $this->getResearchModifier($userid, 2);
		
		
		
		$this->database->query("SELECT TIME_TO_SEC( TIMEDIFF(NOW(), last_updated) ) AS period,
								gold_income, whitecloud_income, thundercloud_income, population_growth
								FROM user_resources_progress WHERE user_id = :userid",
								array(':userid' => $userid));
		if($row = $this->database->fetch()) {
			$period = $row['period'];
			$gold = $period*$row['gold_income']*$harborLevel*$grm;
			$whitecloud = $period*$row['whitecloud_income']*$quarryLevel*$wrm;
			$thundercloud = $period*$row['thundercloud_income']*$quarryLevel*$trm;
			$population = $period*$row['population_growth']*$villageLevel;
		
			$this->database->query("UPDATE user_resources SET
									gold = TRUNCATE(gold + :gold,0),
									whitecloud_essence = TRUNCATE(whitecloud_essence + :whitecloud,0),
									thundercloud_essence = TRUNCATE(thundercloud_essence + :thundercloud,0)
									WHERE user_id = :userid",
									array(':gold' => $gold, 'whitecloud' => $whitecloud,
										  ':thundercloud' => $thundercloud, ':userid' => $userid));
			
			$this->database->query("UPDATE countries SET
									population = TRUNCATE(population + :population,0)
									WHERE user_id = :userid",
									array(':population' => $population, ':userid' => $userid));
		}
		$this->database->query("UPDATE user_resources_progress SET
								last_updated = NOW()
								WHERE user_id = :userid",
								array(':userid' => $userid));
	}
	
	/*
	 * getBuildingInProgress($userid) : Returns all of the users buildings that are being currently upgraded
	 * 		$userid : The ID of the user whose buildings are required
	 */
	private function getBuildingsInProgress($userid) {
		$list = array();
		$this->database->query("SELECT id, building_id FROM user_building_progress
								WHERE user_id = :userid AND enddate<NOW()",
								array(':userid' => $userid));
		while ($row = $this->database->fetch()) {
			$list[] = array("id" => $row['id'],
					"building_id" => $row['building_id']);
		}
		return $list;
	}
	
	/*
	 * upgrade($userid, $buildingid) : Upgrades the building if the necessary resources are available
	 * 		$userid : The ID of the user whose building is to be upgraded
	 * 		$buildingid : The ID of the building which is to be upgraded
	 */
	public function upgrade($userid, $buildingid) {
		$current_level = $this->getCurrentLevel($userid, $buildingid);
		$whitecloud_essence = $this->getBuildingCost($buildingid, $current_level + 1);
		if ($whitecloud_essence == null) {
			return null;
		}
		if ($this->isEnough($userid, $whitecloud_essence, 0)) {
			$this->database->query("UPDATE user_resources SET
									whitecloud_essence=whitecloud_essence - :whitecloud_essence
									WHERE user_id = :userid",
									array(':whitecloud_essence' => $whitecloud_essence, ':userid' => $userid));
			$timeForCompletion = $this->getEstimatedBuildTime($userid, $whitecloud_essence);
			$this->database->query("INSERT INTO user_building_progress
									VALUES(NULL, :userid, :buildingid, 
									:current_level, NOW() + INTERVAL :timeforcompletion SECOND)",
									array(':userid' => $userid, ':buildingid' => $buildingid,
										  ':current_level' => $current_level + 1, 'timeforcompletion' => $timeForCompletion));
			return true; //success
		}else{
			return false; //fail
		}
	}
	
	/*
	 * getBuildingCost($buildingid, $level) : Returns the whitecloud essence required for the upgrade
	 * 		$buildingid : The ID of the building whose info is required
	 * 		$level : The level of the building to which it will be upgraded
	 */
	private function getBuildingCost($buildingid, $level) {
		$this->database->query("SELECT * FROM building_prices
								WHERE building_id = :buildingid AND level = :level",
								array(':buildingid' => $buildingid, ':level' => $level));
		if($row = $this->database->fetch()) {
			return $row['whitecloud_essence'];
		}		
		return null;
	}
	
	/*
	 * getEstimatedBuildTime($whitecloud_essence) : Returns the time needed for upgrade in seconds depending 
	 * 												on the amount of resource required for the upgrade
	 * 		$whitecloud_essence : The amount of resource required for the upgrade
	 */
	private function getEstimatedBuildTime($userid, $whitecloud_essence) {
		$buildTime = $whitecloud_essence/2;
		
		$this->database->query("SELECT population FROM countries
								WHERE user_id = :userid",
								array(':userid' => $userid));
		$row = $this->database->fetch();						
		
		$populationModifier = $row['population']/$whitecloud_essence;
		
		$buildTime *= $populationModifier;
		
		return $buildTime;
	}
	
	/*
	 * getResearches($userid) : Get all researches and their status (completed/not)
	 * 		$userid : The ID of the user whose research status is required
	 */
	public function getResearches($userid) {
		$researches = array();
		$this->database->query("SELECT * FROM research");
		while ($row = $this->database->fetch()) {
			$research = new ResearchDTO();
			$research->id = $row['id'];
			$research->name = $row['name'];
			$research->thundercloud_essence = $row['price'];
			$researches[] = $research;
		}
		foreach ($researches as $research) {
			$research->isCompleted = $this->getCompletion($userid, $research->id);
		}

		return $researches;
	}
	
	/*
	 * getCompletion($userid, $researchid) : Returns the completion status of a research(true => completed)
	 * 		$userid : The ID of the user whose research status is required
	 * 		$researchid : The ID of the research whose status is required
	 */
	private function getCompletion($userid, $researchid) {
		$this->database->query("SELECT * FROM user_researches
								WHERE research_id = :researchid AND user_id = :userid",
								array(':researchid' => $researchid, 'userid' => $userid));
		if ($row = $this->database->fetch()) {
			return true;
		}
		return false;
	}
	
	/*
	 * doResearch($userid, $researchid) : Completes a research if the necessary resources are available
	 * 		$userid : The ID of the user who is completing the research
	 * 		$researchid : The ID of the research being completed
	 */
	public function doReseach($userid, $researchid) {
		$researchCost = $this->getResearchCost($researchid);
		if ($this->isEnough($userid, $researchCost, 1)) {
			$this->database->query("UPDATE user_resources SET
									thundercloud_essence = thundercloud_essence - :researchCost
									WHERE user_id = :userid",
									array(':researchCost' => $researchCost, ':userid' => $userid));
			$this->database->query("INSERT INTO user_researches
									VALUES(NULL, :userid, :researchid)",
									array(':userid' => $userid, 'researchid' => $researchid));
			return true; //success
		}else{
			return false; //fail
		}
	}
	
	
	/*
	 * getResearchCost($researchid) : Returns the amount of thundercloud essence required for completing
	 * 								  a certain research
	 * 		$researchid : The ID of the research whose cost is required
	 */
	private function getResearchCost($researchid) {
		$this->database->query("SELECT price FROM research WHERE id = :id",
								array(':id' => $researchid));		
		$resources = new ResourcesDTO();
		if ($row = $this->database->fetch()) {
			$resources->thundercloud_essence = $row['price'];
			return $resources->thundercloud_essence;
		}
		return null;
	}
	
	/*
	 * getCurrentLevel($userid, $buildingid) : Returns the current level of a certain building
	 * 		$userid : The ID of the user whose building level is required
	 * 		$buildingid : The ID of the building whose current level is required
	 */
	private function getCurrentLevel($userid, $buildingid) {
		$this->database->query("SELECT current_level FROM user_buildings
								WHERE user_id = :userid AND building_id = :buildingid",
								array(':userid' => $userid, ':buildingid' => $buildingid));
		$row = $this->database->fetch();
		return $row['current_level'];
	}
	
	
	/*
	 * getResearchModifier($userid, $flag) : Returns a basic income modifier depending on researches
	 * 		$userid : The ID of the user whose researches are being checked
	 * 		$flag : 0 => researches modifying whitecloud gain
	 * 				1 => researches modifying thunderloud gain
	 * 				2 => researches modifying gold gain
	 */
	private function getResearchModifier($userid, $flag) {
		$modifier = 1;
		switch($flag) {
			case 0 : 
					$this->database->query("SELECT id FROM user_researches
											WHERE user_id = :userid AND research_id = 1",
											array(':userid' => $userid));
					if($this->database->fetch()) $modifier += 0.06;
					$this->database->query("SELECT id FROM user_researches
											WHERE user_id = :userid AND research_id = 2",
											array(':userid' => $userid));
					if($this->database->fetch()) $modifier += 0.1;
					break;
			case 1 : 
					$this->database->query("SELECT id FROM user_researches
											WHERE user_id = :userid AND research_id = 3",
											array(':userid' => $userid));
					if($this->database->fetch()) $modifier += 0.04;
					$this->database->query("SELECT id FROM user_researches
											WHERE user_id = :userid AND research_id = 4",
											array(':userid' => $userid));
					if($this->database->fetch()) $modifier += 0.08;
					break;
			case 2 : 
					$this->database->query("SELECT id FROM user_researches
											WHERE user_id = :userid AND research_id = 5",
											array(':userid' => $userid));
					if($this->database->fetch()) $modifier += 0.05;
					break;
		}
		return $modifier;
	}
	
	/*
	 * getRequesets($userid) : Returns all trade requests that the user is taking part in
	 * 		$userid : The ID of the user whose trade requests are required
	 */
	public function getRequests($userid) {
		$requests = array();
		$this->database->query("SELECT * FROM trade_requests
								WHERE reciever_id = :userid
								AND answer = 0",
								array(':userid' => $userid));
		while ($row = $this->database->fetch()) {
			$request = new RequestDTO();
			$request->id = $row['id'];
			$request->sender = $row['sender_id'];
			$request->senderName = $this->getUsernameById($row['sender_id']);
			$request->reciever = $row['reciever_id'];
			$request->offer = $row['offer'];
			$request->whitecloud = $row['demand_whitecloud'];
			$request->thundercloud = $row['demand_thundercloud'];
			$request->answer = $row['answer'];
			$requests[] = $request;
		}
		return $requests;
	}
	
	/*
	 * makeRequest($userid, $recieverid, $offer, $whitecloud, $thundercloud) : Submits a trading request
	 * 		$userid : The ID of the player sending the request
	 * 		$recieverid : ID of the player targeted for the transaction
	 * 		$offer : The amount of gold the user is giving
	 * 		$whitecloud : The amount of whitecloud essence the user requires to make the transaction
	 * 		$thundercloud : The amount of thundercloud essence the user requires to make the transaction
	 */
	public function makeRequest($userid, $recieverid, $offer, $whitecloud, $thundercloud) {
		if($this->isEnough($userid, $offer, 2)){
			$this->database->query("INSERT INTO trade_requests
									VALUES(NULL, :userid, :recieverid, :offer, :whitecloud, :thundercloud, 0)",
									array(':userid' => $userid, ':recieverid' => $recieverid, ':offer' => $offer,
										  ':whitecloud' => $whitecloud, ':thundercloud' => $thundercloud));
			$this->database->query("UPDATE user_resources SET
									gold = gold - :offer
									WHERE user_id = :userid",
									array(':offer' => $offer, ':userid' => $userid));
			return true;
		}else{
			return false;
		}		
			
	}
	
	/*
	 * answerRequest($requestid, $answer) : Finalizing the trade by answering the request
	 * 		$requestid : The ID of the trade request that is being answered
	 * 		$answer : The answer to the trade request (1 => yes; 2 => no)
	 */
	public function answerRequest($requestid, $answer) {
		$this->database->query("SELECT * FROM trade_requests
								WHERE id = :requestid",
								array(':requestid' => $requestid));
		
		$request = new RequestDTO();
		if($row = $this->database->fetch()) {
			$request->id = $row['id'];
			$request->sender = $row['sender_id'];
			$request->reciever = $row['reciever_id'];
			$request->offer = $row['offer'];
			$request->whitecloud = $row['demand_whitecloud'];
			$request->thundercloud = $row['demand_thundercloud'];
			$request->answer = $row['answer'];			
		}
		
		if($answer == 1){
			if ($this->isEnough($request->reciever, $request->whitecloud, 0) &&
				$this->isEnough($request->reciever, $request->thundercloud, 1)) {
					$this->database->query("UPDATE trade_requests SET
											answer = :answer WHERE id = :requestid",
											array(':answer' => $answer, ':requestid' => $requestid));
					$this->database->query("UPDATE user_resources SET
											whitecloud_essence = whitecloud_essence - :whitecloud,
											thundercloud_essence = thundercloud_essence - :thundercloud
											WHERE user_id = :userid",
											array(':whitecloud' => $request->whitecloud, 
												  ':thundercloud' => $request->thundercloud,
												  ':userid' => $request->reciever));
					$this->database->query("UPDATE user_resources SET
											whitecloud_essence = whitecloud_essence + :whitecloud,
											thundercloud_essence = thundercloud_essence + :thundercloud
											WHERE user_id = :userid",
											array(':whitecloud' => $request->whitecloud, 
												  ':thundercloud' => $request->thundercloud,
												  ':userid' => $request->sender));
					return true;
			}			
		}else{
			$this->database->query("UPDATE user_resources SET
									gold = gold + :offer
									WHERE user_id = :userid",
									array(':offer' => $request->offer, ':userid' => $request->sender));
			return true;
		}
		
		return false;		
	}
	
	
	/*
	 * isEnough($userid, $cost, $flag) : Checks if the user has the necessary resources to complete the action
	 * 		$userid : The ID of the user whose info is needed
	 * 		$cost : The amount of resource required to complete the action
	 * 		$flag : (0 => whitecloud essence, 1 => thundercloud essence, 2 => gold)
	 */
	private function isEnough($userid, $cost, $flag) {
		switch ($flag) {
			
			case 0 : 
					$this->database->query("SELECT whitecloud_essence FROM user_resources
											WHERE whitecloud_essence >= :cost
											AND user_id = :userid",
											array(':cost' => $cost, ':userid' => $userid));
					if ($row = $this->database->fetch()) return true;
					break;
					 
			case 1 : 
					$this->database->query("SELECT thundercloud_essence FROM user_resources
											WHERE thundercloud_essence >= :cost
											AND user_id = :userid",
											array(':cost' => $cost, ':userid' => $userid));
					if ($row = $this->database->fetch()) return true;
					break;
					 
			case 2 : 
					$this->database->query("SELECT gold FROM user_resources
											WHERE gold >= :cost
											AND user_id = :userid",
											array(':cost' => $cost, ':userid' => $userid));
					if ($row = $this->database->fetch()) return true;
					break;
		}
		return false;
	}
	
	/*
	 * getUsernameById($userid) : Get username by Id
	 *		$userid : The ID of the user whose username is requested
	 */
	private function getUsernameById($userid) {
		$this->database->query("SELECT username FROM users
								WHERE id = :userid",
								array(':userid' => $userid));
		$row = $this->database->fetch();
		return $row['username'];
	}
	
	public function getSingleBuilding($userid, $buildingid) {
		$this->database->query("SELECT current_level FROM user_buildings
								WHERE user_id = :userid
								AND building_id = :buildingid",
								array(':userid' => $userid, ':buildingid' => $buildingid));
								
		$buildings = array();
		
		if($row = $this->database->fetch()) {
			$building = new BuildingDTO();
			$building->id = $buildingid;
			$building->currentLevel = $row['current_level'];
			$building->maxLevel = $this->getMaxLevel($building->id);
			$building_progress = $this->getProgress($userid, $building->id);
			$building->inProgress = $building_progress['inProgress'];
			$building->endTime = $building_progress['endTime'];
			$building->nextLevelEssence = $this->getNextLevelEssence($building->id, $building->currentLevel + 1);
			$buildings[] = $building;
		}		
		
		return $buildings;
	}
}