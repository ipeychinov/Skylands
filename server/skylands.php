<?php
session_start();
require_once("libraries/service.php");
require_once("libraries/model.php");
require_once("libraries/entity.php");

$request_text = $_POST["request"];
$request = json_decode($request_text, true);
$response = '';
switch($request['id']) {
	/*
	 * case 1 : Login
	 */
	case 1 : 
			require_once("services/login_service.php");
			$service = new LoginService();
			$response = $service->doLogin($request['username'], $request['password']);
			break;
			
	/*
	 * case 2 : Logout
	 */
	case 2 :
			require_once("services/login_service.php");
			$service = new LoginService();
			$response = $service->doLogout();
			break;
			
	/*
	 * case 3 : Get island info (resorces, building info)
	 */
	case 3 :
			require_once("services/skyland_service.php");
			$service = new SkylandService();
			$response = $service->getIsland();
			break;
	
	/*
	 * case 4 : Upgrade a certain building by ID
	 */
	case 4 :
			require_once("services/skyland_service.php");
			$service = new SkylandService();
			$response = $service->upgradeBuilding($request['building_id']);
			break;
			
	/* 
	 * case 5 : Upgrade Research by ID
	 */
	case 5 :
			require_once("services/skyland_service.php");
			$service = new SkylandService();
			$response = $service->upgradeResearch($request['research_id']);
			break;
			
	/*
	 * case 6 : Get every countrys coordinates
	 */
	case 6 :
			require_once ("services/map_service.php");
			$service = new MapService();
			$response = $service->getMapCoordinates();
			break;
			
	/*
	 * case 7 : Get a certain countrys info by its coordinatesID
	 */
	case 7 :
			require_once ("services/map_service.php");
			$service = new MapService();
			$response = $service->getLandInfoByCoordinates($request['coordinates_id']);
			break;
			
	/*
	 * case 8 : Create new user
	 */
	case 8 :
			require_once ("services/login_service.php");
			$service = new LoginService();
			$response = $service->doRegister($request['username'], $request['password']);
			break;
			
	/*
	 * case 9 : Set country name, anthem, role, political system and religion
	 */
	case 9 :
			require_once ("services/login_service.php");
			$service = new LoginService();
			$response = $service->changeAll($request['name'], $request['anthem'], $request['role'],
											$request['political_system'], $request['religion']);
			break;
	
	/*
	 * case 10 : Get all researches(completed or not) and their info
	 */
	case 10 :
			require_once ("services/skyland_service.php");
			$service = new SkylandService();
			$response = $service->getResearches();
			break;
			
	/*
	 * case 11 : Make a trading request to another player
	 * 			 (you offer gold in exchange for either of the other recources)
	 */
	case 11 :
			require_once ("services/skyland_service.php");
			$service = new SkylandService();
			$response = $service->makeRequest($request['reciever_id'], $request['offer'], 
											  $request['demand_whitecloud'], $request['demand_thundercloud']);
			break;
			
	/*
	 * case 12 : Answer a trading request from another player
	 * 			 (answers: confirm, deny)
	 */
	case 12 :
			require_once ("services/skyland_service.php");
			$service = new SkylandService();
			$response = $service->answerRequest($request['request_id'], $request['answer']);
			break;
			
	/*
	 * case 13 : Get all unanswered trade requests that the user is taking part in
	 */
	case 13 :
			require_once ("services/skyland_service.php");
			$service = new SkylandService();
			$response = $service->getRequests();
			break;
	
	/*
	 * case 14 : 
	 */
	case 14 :
			require_once ("services/skyland_service.php");
			$service = new SkylandService();
			$response = $service->getBuildingInfo($request['building_id']);
			break;
	
}
echo $response;