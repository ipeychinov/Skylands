<?php
class ErrorDTO {
	public $code;
	public $name;
	public function __construct($name, $code) {
		$this->name = $name;
		$this->code = $code;
	}
}