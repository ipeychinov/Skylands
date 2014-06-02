<?php
class LoginModel extends Model {
	
	public function __construct() {
		parent::__construct();
	}	
	
	/*
	 * isValidCredentials($username, $password) : Check if the username and password match
	 * 		$username : The user inputed username
	 * 		$password : The user inputed password
	 */
	public function isValidCredentials($username, $password) {
		$this->database->query("SELECT id FROM users
								WHERE username = :username AND password = MD5(:password)",
								array(':username' => $username,
									  ':password' => $password));
		if ($this->database->fetch()) {
			return true;
		}
		return false;
	}
	
	/*
	 * getIDbyUsername($username) : Returns the ID assigned to the username
	 * 		$username : The username whose ID is required
	 */
	public function getIDbyUsername($username) {
		$this->database->query("SELECT id FROM users WHERE username = :username",
								array(':username' => $username));
		if ($row = $this->database->fetch()) {
			return $row['id'];
		}
		return 0;
	}
	
	/*
	 * isUsernameAvailable($username) : Checks if the username is taken
	 * 		$username : The username being checked
	 */
	public function isUsernameAvailable($username) {
		$this->database->query("SELECT username FROM users
								WHERE username = :username",
								array(':username' => $username));
		if ($this->database->fetch()) {
			return false;
		}
		return true;
	}
	
	/*
	 * isNameAvailable($name) : Checks if the country name is taken
	 * 		$name : The name being checked
	 */
	public function isNameAvailable($name) {
		$this->database->query("SELECT name FROM countries
								WHERE name = :name",
								array(':name' => $name));
		if ($row = $this->database->fetch()) {
			return false;
		}
		return true;
	}
	
	/*
	 * isPositionAvailable($x, $y) : Checks if the coordinates are taken
	 * 		$x : X axis coordinate
	 * 		$y : Y axis coordinate
	 */
	private function isPositionAvailable($x, $y) {
		$this->database->query("SELECT * FROM coordinates
								WHERE x_axis = :x AND y_axis = :y",
								array(':x' => $x, ':y' => $y));
		if ($this->database->fetch()) {
			return false;
		}
		return true;
	}
	
	/*
	 * setupUser($username, $password) : Sets up a new user with the player input
	 * 		$username : The username of the new user
	 * 		$password : The password of the new user
	 */
	public function setupUser($username, $password) {
		$this->database->query("INSERT INTO users
								VALUES(NULL, :username, MD5(:password))",
								array(':username' => $username,
									  ':password' => $password));
		
		$userid = $this->getIDbyUsername($username);
		$coordinatesid = $this->setupPosition();
		
		$this->setupCountry($userid, $coordinatesid);	
		$this->setupBuildings($userid);
		$this->setupResources($userid);
	}
	
	/*
	 * setupCountry($userid, $coordinatesid) : Sets up a basic country info and inserting it into the DB
	 * 		$userid : The ID of the user whose country info is being setup
	 * 		$coodrinatesid : The ID of the coordinates related to the country
	 */
	private function setupCountry($userid, $coordinatesid) {
		$this->database->query("INSERT INTO countries
								VALUES(NULL, :userid, 'unknown', 'unknown', 'unknown', unknown, unknown, :coordinatesid, 100)",
								array(':userid' => $userid, ':coordinatesid' => $coordinatesid));
	}

	/*
	 * setupPosition() : Sets up the coordinates for the new country
	 */
	private function setupPosition() {
		$x = 0; $y = 0;
		$available = false;
		while (! $available) {
			$x = rand(-100, 100);
			$y = rand(-100, 100);
			$available = $this->isPositionAvailable($x, $y);
		}
		$this->database->query("INSERT INTO coordinates
								VALUES(NULL, :x, :y)",
								array(':x' => $x, ':y' => $y));
		$this->database->query("SELECT id FROM coordinates
								WHERE x_axis = :x AND y_axis = :y",
								array(':x' => $x, ':y' => $y));
		$row = $this->database->fetch();
		return $row['id'];
	}
	
	/*
	 * setupBuildings($userid) : Sets up the new countrys buildings to start level(1)
	 * 		$userid : The ID of the user to whom the buildings belong
	 */
	private function setupBuildings($userid) {
		for($i = 1; $i <= 5; $i++) {
			$this->database->query("INSERT INTO user_buildings
									VALUES(NULL, :userid, :buildingid, :level)",
									array(':userid' => $userid, ':buildingid' => $i, ':level' => 1));
		}
	}
	
	/*
	 * setupResources($userid) : Sets up the starting user resources & the 1st resource gain update
	 * 		$userid : The ID of the user whose resources are being set
	 */
	private function setupResources($userid) {
		$this->database->query("INSERT INTO user_resources
								VALUES(NULL, :userid, 100, 100, 80)",
								array(':userid' => $userid));
		$this->database->query("INSERT INTO user_resources_progress
								VALUES(NULL, :userid, 0.001, 0.01, 0.005, 0.0005, NOW())",
								array(':userid' => $userid));
	}
	
	/*
	 * changeName($userid, $name) : Changes the name of the country
	 * 		$userid : The ID of the user whose country info is being changed
	 * 		$name : The new name for the country
	 */
	public function changeName($userid, $name) {
		$this->database->query("UPDATE countries SET name = :name
								WHERE user_id = :userid",
								array(':name' => $name, ':userid' => $userid));
	}
	
	/*
	 * changeAnthem($userid, $anthem) : Changes the anthem of the country
	 * 		$userid : The ID of the user whose country info is being changed
	 * 		$anthem : The new anthem for the country
	 */
	public function changeAnthem($userid, $anthem) {
		$this->database->query("UPDATE countries SET anthem = :anthem
								WHERE user_id = :userid",
								array(':anthem' => $anthem, ':userid' => $userid));
	}
	
	/*
	 * changeRole($userid, $role) : Changes the role of the player
	 * 		$userid : The ID of the user whose country info is being changed
	 * 		$role : The new role for the player
	 */
	public function changeRole($userid, $role) {
		$this->database->query("UPDATE countries SET role = :role
								WHERE user_id = :userid",
								array(':role' => $role, ':userid' => $userid));
	}
	
	/*
	 * changeSystem($userid, $systemid) : Changes the political system of the country
	 * 		$userid : The ID of the user whose country info is being changed
	 * 		$system : The new political system for the country
	 */
	public function changeSystem($userid, $system) {
		$this->database->query("UPDATE countries SET political_system = :system
								WHERE user_id = :userid",
								array(':system' => $system, ':userid' => $userid));
	}
	
	/*
	 * changeReligion($userid, $religionid) : Changes the religion of the country
	 * 		$userid : The ID of the user whose country info is being changed
	 * 		$religion : The new religion for the country
	 */
	public function changeReligion($userid, $religion) {
		$this->database->query("UPDATE countries SET religion = :religion
								WHERE user_id = :userid",
								array(':religion' => $religion, ':userid' => $userid));
	}
}