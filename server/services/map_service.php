<?php
require_once ('dto/otherland_dto.php');
require_once ('dto/coordinates_dto.php');
require_once ('models/map_model.php');
class MapService extends Service {
	
	public function __construct() {
		parent::__construct();
	}
	
	/*
	 * getMapCoordinates() : Returns all map coordinates and their IDs
	 */
	public function getMapCoordinates() {
		$map_model = new MapModel();
		$response = $map_model->getAllCoordinates();
		
		return json_encode($response);
	}
	
	/*
	 * getLandInfoByCoordinates($coordinates_id) : Returns the targeted countrys info by coordinates id
	 * 		$coordinates_id : The ID of the coordinates of the targeted country
	 */
	public function getLandInfoByCoordinates($coordinates_id) {
		$map_model = new MapModel();
		$response = new OtherlandDTO();
		
		$response->id = 7;
		$response->userId = $map_model->getUserId($coordinates_id);
		$response->username = $map_model->getUsername($coordinates_id);
		$response->name = $map_model->getCountryName($coordinates_id);
		$response->userrole = $map_model->getRole($coordinates_id);
		$response->anthem = $map_model->getAnthem($coordinates_id);
		$response->political_system = $map_model->getSystem($coordinates_id);
		$response->religion = $map_model->getReligion($coordinates_id);
		
		return json_encode($response);
	}
}