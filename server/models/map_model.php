<?php
class MapModel extends Model {
	
	public function __construct() {
		parent::__construct();
	}
	
	/*
	 * getAllCoordinates() : Returns all map coordinates
	 */
	public function getAllCoordinates() {
		$coordinates = array();
		$this->database->query("SELECT * FROM coordinates");
		while ($row = $this->database->fetch()) {
			$coordinate = new CoordinatesDTO();
			$coordinate->id = $row['id'];
			$coordinate->x = $row['x_axis'];
			$coordinate->y = $row['y_axis'];
			$coordinates[] = $coordinate;
		}
		
		foreach($coordinates as $coordinate) {
			$coordinate->countryName = $this->getCountryName($coordinate->id);
		}
		return $coordinates;
	}
	
	/*
	 * getUsername($coordinates_id) : Get the username of the player to whom the passed coordinates belong
	 * 		$coordinates_id : The id of the country that is being targeted
	 */
	public function getUsername($coordinates_id){
		$username = '';
		$this->database->query("SELECT username FROM users
								WHERE id = (SELECT user_id FROM countries
											WHERE coordinates_id = :coordinatesid)",
								array(':coordinatesid' => $coordinates_id));
		$row = $this->database->fetch();
		$username = $row['username'];
		return $username;
	}
	
	/*
	 * getCountryName($coordinates_id) : Get the name of the country to whom the passed coordinates belong
	 * 		$coordinates_id : The id of the country that is being targeted
	 */
	public function getCountryName($coordinates_id){
		$countryname = '';
		$this->database->query("SELECT name FROM countries
								WHERE coordinates_id = :coordinatesid",
								array(':coordinatesid' => $coordinates_id));
		$row = $this->database->fetch();
		$countryname = $row['name'];
		return $countryname;
	}
	
	/*
	 * getAnthem($coordinates_id) : Get the anthem of the country to whom the passed coordinates belong
	 * 		$coordinates_id : The id of the country that is being targeted
	 */
	public function getAnthem($coordinates_id){
		$anthem = '';
		$this->database->query("SELECT anthem FROM countries
								WHERE coordinates_id = :coordinatesid",
								array(':coordinatesid' => $coordinates_id));
		$row = $this->database->fetch();
		$anthem = $row['anthem'];
		return $anthem;
	}
	
	/*
	 * getSystem($coordinates_id) : Get the political system of the country to whom the passed coordinates belong
	 * 		$coordinates_id : The id of the country that is being targeted
	 */
	public function getSystem($coordinates_id){
		$system = '';
		$this->database->query("SELECT political_system FROM countries
								WHERE coordinates_id = :coordinatesid",
								array(':coordinatesid' => $coordinates_id));
		$row = $this->database->fetch();
		$system = $row['political_system'];
		return $system;
	}
	
	/*
	 * getReligion($coordinates_id) : Get the religion of the country to whom the passed coordinates belong
	 * 		$coordinates_id : The id of the country that is being targeted
	 */
	public function getReligion($coordinates_id){
		$religion = '';
		$this->database->query("SELECT religion FROM countries
								WHERE coordinates_id = :coordinatesid",
								array(':coordinatesid' => $coordinates_id));
		$row = $this->database->fetch();
		$religion = $row['religion'];
		return $religion;
	}
	
	/*
	 * getRole($coordinates_id) : Get the role of the player to whom the passed coordinates belong
	 * 		$coordinates_id : The id of the country that is being targeted
	 */
	public function getRole($coordinates_id){
		$role = '';
		$this->database->query("SELECT role FROM countries
								WHERE coordinates_id = :coordinatesid",
								array(':coordinatesid' => $coordinates_id));
		$row = $this->database->fetch();
		$role = $row['role'];
		return $role;
	}
	
	/*
	 * getUserId($coordinates_id) : Get the id of the player to whom the passed coordinates belong
	 * 		$coordinates_id : The id of the country that is being targeted
	 */
	public function getUserId($coordinates_id){
		$this->database->query("SELECT user_id FROM countries
								WHERE coordinates_id = :coordinatesid",
								array(':coordinatesid' => $coordinates_id));
		$row = $this->database->fetch();
		$userId = $row['user_id'];
		return $userId;
	}
}