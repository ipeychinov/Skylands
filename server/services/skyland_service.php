<?php
require_once("models/skyland_model.php");
require_once("dto/ownland_dto.php");
require_once("dto/response_dto.php");
class SkylandService extends Service {
	
	public function __construct() {
		parent::__construct();
	}
	
	/*
	 * getIsland() : Returns island info (resources, buildings)
	 */
	public function getIsland() {
		$model = new SkylandModel();
		$response = new OwnlandDTO();
		$response->id = 3;
		$model->update($this->session->getUserID());
		$response->resources = $model->resources($this->session->getUserID());
		$response->buildings = $model->buildings($this->session->getUserID());
		
		return json_encode($response);
	}
	
	/*
	 * upgradeBuilding($building_id) : Raises the level of a certain building
	 * 		$building_id : The ID of the building that must be upgradet
	 */
	public function upgradeBuilding($building_id) {
		$model = new SkylandModel();
		$response = new ResponseDTO();
		$response->id = 4;
		$response->error = null;
		$model->update($this->session->getUserID());
		if (!$model->upgrade($this->session->getUserID(), $building_id)) {
			$response->error = new ErrorDTO("Not enough whitecloud essence", "E0004");			
		}
		return json_encode($response);
	}
	
	/*
	 * upgradeResearch($research_id) : Completes a certain research
	 * 		$research_id : The ID of the research that must be completed
	 */
	public function upgradeResearch($research_id) {
		$model = new SkylandModel();
		$response = new ResponseDTO();
		$response->id = 5;
		$response->error = null;
		$model->update($this->session->getUserID());
		if(!$model->doReseach($this->session->getUserID(), $research_id)){
			$response->error = new ErrorDTO("Not enough thundercloud essence", "E0005");
		}		
		return json_encode($response);
	}
	
	/*
	 * getResearches() : Returns all researches (completed or not)
	 */
	public function getResearches() {
		$model = new SkylandModel();
		$response = $model->getResearches($this->session->getUserID());
		
		return json_encode($response);
	}
	
	/*
	 * getRequests() : Returns all requests that the user is taking part in
	 */
	public function getRequests() {
		$model = new SkylandModel();
		$response = $model->getRequests($this->session->getUserID());
		
		return json_encode($response);
	}
	
	/*
	 * makeRequest($reciever, $offer, $whitecloud, $thundercloud) : Submits a trading request
	 * 		$recieverid : ID of the player targeted for the transaction
	 * 		$offer : The amount of gold the user is giving
	 * 		$whitecloud : The amount of whitecloud essence the user requires to make the transaction
	 * 		$thundercloud : The amount of thundercloud essence the user requires to make the transaction
	 */
	public function makeRequest($recieverid, $offer, $whitecloud, $thundercloud) {
		$model = new SkylandModel();
		$response = new ResponseDTO();
		$response->id = 16;
		$response->error = null;
		if(!$model->makeRequest($this->session->getUserID(), $recieverid, $offer, $whitecloud, $thundercloud)) {
			$response->error = new ErrorDTO("Not enough gold", "E0016");
		}
		
		return json_encode($response);
	}
	
	/*
	 * answerRequest($requestid, $answer) : Finalizing the trade by answering the request
	 * 		$requestid : The ID of the trade request that is being answered
	 * 		$answer : The answer to the trade request
	 */
	public function answerRequest($requestid, $answer) {
		$model = new SkylandModel();
		$response = new ResponseDTO();
		$response->id = 17;
		$response->error = null;
		if(!$model->answerRequest($requestid, $answer)){
			$response->error = new ErrorDTO("Not enough resource", "E0017");
		}
		
		return json_encode($response);
	}
	
	public function getBuildingInfo($buildingid) {
		$model = new SkylandModel();
		$response = new OwnlandDTO();
		$response->id = 3;
		$model->update($this->session->getUserID());
		$response->resources = $model->resources($this->session->getUserID());
		$response->buildings = $model->getSingleBuilding($this->session->getUserID(), $buildingid);
		
		return json_encode($response);
	}
}