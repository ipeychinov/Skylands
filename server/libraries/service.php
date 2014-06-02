<?php
require_once ("libraries/session.php");
class Service {
	public $session;
	public function __construct() {
		$this->session = new Session();
	}
}