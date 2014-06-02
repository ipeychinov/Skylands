<?php
class Session {
	
	/*
	 * setUserID($userid) : Assigns a user ID to the session
	 * 		$userid : The ID of the user starting the session
	 */
	public function setUserID($userid) {
		$_SESSION['user_id'] = $userid;
	}
	
	/*
	 * getUserID() : Returns the session user ID
	 */
	public function getUserID() {
		return $_SESSION['user_id'];
	}
}