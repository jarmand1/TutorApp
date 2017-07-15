<?php
include_once 'connection.php';
	
	class User_Retrieve {
		
		private $db;
		private $connection;
		
		 function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
		
		public function get_user_values($email, $is_tutor){
		
				if($is_tutor== 'Y'){	
					$query = "SELECT Tutor_ID FROM tutor WHERE Tutor_Email = '$email'";
					$result = mysqli_query($this->connection, $query);
					if(mysqli_num_rows($result)>0){
						while($row = mysqli_fetch_assoc($result)){
							$tutor_id = $row['Tutor_ID'];
						}	
					$query2 = "SELECT * FROM tutor_availability WHERE Tutor_ID  = '$tutor_id'";
					$result2 = mysqli_query($this->connection, $query2);
					if(mysqli_num_rows($result2)>0){
						$json = array(array("availability" => "not_null"));
						while($row = mysqli_fetch_assoc($result2)){
							$json[] = $row;
						}
					}else{
						$json = array(array("availability" => "null"));
					}
					}else if(empty($json)){
						$json = array(array("failure_availability"  => "user likely does not exist"));		
				 }
				}elseif($is_tutor== 'N'){
					$query = "SELECT Ram_ID FROM student WHERE Student_Email = '$email'";
					$result = mysqli_query($this->connection, $query);
					if(mysqli_num_rows($result)>0){
						while($row = mysqli_fetch_assoc($result)){
							$ram_id = $row['Ram_ID'];
						}	
					$query2 = "SELECT * FROM requested_appointment WHERE Ram_ID  = '$ram_id'";
					$result2 = mysqli_query($this->connection, $query2);
					if(mysqli_num_rows($result2)>0){
						$json = array(array("availability" => "not_null"));
						while($row = mysqli_fetch_assoc($result2)){
							$json[] = $row;
						}
					}else{
						$json = array(array("availability" => "null"));
					}
				}else if(empty($json)){
					$json = array(array("failure_availability" => "user likely does not exist"));	
				}
			}else{	
				$json =  array(array("fatal_error" => "invalid parameters"));
			}
				echo json_encode($json);
				mysqli_close($this -> connection);
				
			
		}
		
	}
	
	
	$user = new User_Retrieve();
	if(isset($_POST['email'], $_POST['is_tutor'])) {
		$email = $_POST['email'];
		$is_tutor = $_POST['is_tutor'];
		
		if(!empty($email) && !empty($is_tutor)){
			$user-> get_user_values($email, $is_tutor);
			
		}else{
			$json_error= array(array("error_get_info" => " missing data "));
			echo json_encode($json_error);
		}
		
	}
	


		