<?php
require_once("models/login_model.php");
require_once("dto/response_dto.php");
class LoginService extends Service {
	
	public function __construct() {
		parent::__construct();
	}
	
	/*
	 * doLogin($username, $password) : Login if the username and password match
	 * 		$username : Username input
	 * 		$password : Password input
	 */
	public function doLogin($username, $password) {
		$login_model = new LoginModel();
		$response = new ResponseDTO();
		$response->id = 1;
		if ($login_model->isValidCredentials($username, $password)) {
			$id = $login_model->getIDbyUsername($username);
			$this->session->setUserID($id);
			//
			$response->error = null;
		}else{
			$response->error = new ErrorDTO("Wrong username/password.", "E0001");
		}
		return json_encode($response);
	}
	
	/*
	 * doLogout() : Logout & destroy session
	 */
	public function doLogout() {
		$this->session->setUserID(0);
		session_destroy();
		//TODO
		return '{"id" : 2}';
	}
	
	/*
	 * doRegister($username, $password) : Setup a new user by user inputed username and password
	 * 		$username : Username input
	 * 		$password : Password input
	 */
	public function doRegister($username, $password) {
		$login_model = new LoginModel();
		$response = new ResponseDTO();
		$response->id = 8;
		if ($login_model->isUsernameAvailable($username)) {
			$login_model->setupUser($username, $password);
			$this->doLogin($username, $password);
			
			$response->error = null;
		}else{
			$response->error = new ErrorDTO("Username taken", "E0008");
		}
		return json_encode($response);
	}
	
	/*
	 * changeAll($name, $anthem, $role, $system, $religion) : Setup a new country or change all its info
	 * 		$name : Country name
	 * 		$anthem : Country anthem
	 * 		$role : Player role
	 * 		$system : The new political system
	 * 		$religionid : The new religion
	 */
	public function changeAll($name, $anthem, $role, $system, $religion) {
		$login_model = new LoginModel();
		$response = new ResponseDTO();
		$response->id = 9;
		$response->error = null;
		if ($login_model->isNameAvailable($name)) {
			$login_model->changeName($this->session->getUserID(), $name);
			$login_model->changeAnthem($this->session->getUserID(), $anthem);
			$login_model->changeRole($this->session->getUserID(), $role);
			$login_model->changeSystem($this->session->getUserID(), $system);
			$login_model->changeReligion($this->session->getUserID(), $religion);
		}else{
			$response->error = new ErrorDTO("Country name taken", "E0009");
		}
		return json_encode($response);
	}
}