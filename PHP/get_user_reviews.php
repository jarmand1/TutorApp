<?php
include_once 'connection.php';
	
	class User_Retrieve_Reviews {
		
		private $db;
		private $connection;
		
		 function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
		
		public function get_user_reviews($tutor_app_id)
		{
			$query = "SELECT * FROM tutor_availability WHERE Tutor_Appointment_ID = '$tutor_app_id'";
			$result = mysqli_query($this->connection, $query);
			if(mysqli_num_rows($result)>0){
				while($row = mysqli_fetch_assoc($result)){
					$tutor_id = $row['Tutor_ID'];	
				}
			
				if(!isset($tutor_id)){
					$json = array(array("error_get_reviews" => "No reviews exist"));
				}else{
					$query2 = "SELECT Appointment_ID FROM appointment WHERE Tutor_ID = '$tutor_id'";
					$result2 = mysqli_query($this->connection, $query2);
					if(mysqli_num_rows($result2)>0){
						while($row2 = mysqli_fetch_assoc($result2)){
							$app_id = $row2['Appointment_ID'];
							$query3 = "SELECT Review, Rating FROM review WHERE Appointment_ID = '$app_id'";
							$result3 = mysqli_query($this->connection, $query3);
							if(mysqli_num_rows($result3)>0){
								while($row3 = mysqli_fetch_assoc($result3)){
									$json[] = $row3;
								}
							}
						}
					}
				}	
				if(!isset($json)){
					$json = array(array("error_get_reviews" => "No reviews found"));	
				}
				echo json_encode($json);
				mysqli_close($this -> connection);
			}else{
            
			$json = array(array("error_get_reviews" => "Bad"));	
			echo json_encode($json);
			mysqli_close($this->connection);
			}
			
		} //END FUNCTION
		
	} //END CLASS
	
	
	$user = new User_Retrieve_Reviews();
	if(isset($_POST['tutor_app_id'])) {
		$tutor_app_id = $_POST['tutor_app_id'];
		
		if(!empty($tutor_app_id)){
			$user->get_user_reviews($tutor_app_id);
			
		}else{
			$json_error = array(array("error_get_reviews" =>  "missing data"));
			echo json_encode($json_error);
		}
		
	}else{
		$json_error = array(array("error_get_reviews" =>  "missing data"));
			echo json_encode($json_error);
	}
	


		