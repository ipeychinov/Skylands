<?php
class Database {
	private $pdo;
	private $result;
	
	/*
	 * __construct() : Sets up a connection to the database
	 */
	public function __construct() {
		$dsn = 'mysql:host=localhost;dbname=skylands';
		try {
			$this->pdo = new PDO($dsn, 'root', '');
		}catch (PDOException $e){
			echo 'Connection failed: ' . $e->getMessage();
		}
	}
	
	/*
	 * query($sql, $passarg = NULL) : Executes a query
	 * 		$sql : The sql query
	 * 		$passarg : Optional arguments
	 */
	public function query($sql, $passarg = NULL) {
		$pstmt = $this->pdo->prepare($sql);
		if ($passarg != NULL) {
			$pstmt->execute($passarg);
		} else {
			$pstmt->execute();
		}
		$this->result = $pstmt;
	}
	
	/*
	 * fetch() : returns the result of the last query
	 */
	public function fetch() {
		if (!$this->result) {
			return null;
		}
		return $this->result->fetch(PDO::FETCH_ASSOC);
	}
}