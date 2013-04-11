<?php
	class inspection{
		private $id;
		private $name;
		private $name2;
		private $address;
		private $address2;
		private $cityst;
		private $phone;
		private $zip;
		private $lat;
		private $long;

		function set_id($new_id){
			$this->id = $new_id;
		}
		function get_id(){
			return $this->id;
		}
		function set_name($new_name){
			$this->name = $new_name;
		}
		function get_name(){
			return $this->name;
		}
		function set_name2($new_name2){
			$this->name2 = $new_name2;
		}
		function get_name2(){
			return $this->name2;
		}
		function set_address($new_address){
			$this->address = $new_address;
		}
		function get_address(){
			return $this->address;
		}
		function set_address2($new_address2){
			$this->address2 = $new_address2;
		}
		function get_address2(){
			return $this->address2;
		}
		function set_cityst($new_cityst){
			$this->cityst = $new_cityst;
		}
		function get_cityst(){
			return $this->cityst;
		}
		function set_phone($new_phone){
			$this->phone = $new_phone;
		}
		function get_phone(){
			return $this->phone;
		}
		function set_zip($new_zip){
			$this->zip = $new_zip;
		}
		function get_zip(){
			return $this->zip;
		}
		function set_lat($new_lat){
			$this->lat = $new_lat;
		}
		function get_lat(){
			return $this->lat;
		}
		function set_long($new_long){
			$this->long = $new_long;
		}
		function get_long(){
			return $this->long;
		}
	}
	?>