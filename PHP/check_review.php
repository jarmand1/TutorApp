<?php
include_once 'connection.php';
	
	class  User_Check_Review {
		
		private $db;
		private $connection;
		
		 function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
		
		public function check_review($app_id)
		{
				//First check appointment to make sure the appointment exists
				$query = "SELECT * FROM appointment WHERE Appointment_ID='$app_id'";
				$result = mysqli_query($this->connection, $query);
				if(mysqli_num_rows($result)>0){
					//now check the review table to make sure the user has not reviewed that appointment yet
					$query2 = "SELECT * FROM review WHERE  Appointment_ID='$app_id'";
					$result2 = mysqli_query($this->connection, $query2); 
					if(mysqli_num_rows($result2)==0){
						//we're good, lets insert
						$json['success_review_check'] = ' no review exists ';
					}else{
						$json['error_review_check'] = ' review for this appointment already exists';
					}
					echo json_encode($json);
					mysqli_close($this -> connection);
				}else{
					$json['error_review'] = ' query failed, appointment does not exist '.mysqli_error($this->connection);	
					echo json_encode($json);
					mysqli_close($this->connection);
				}

		} //END FUNCTION
	} //END CLASS	
	
	
	
	$user = new User_Check_Review();
	if(isset($_POST['app_id'])){
			$app_id = $_POST['app_id'];

		if(!empty($app_id)){
			$user->check_review($app_id);
		}else{
			$json_error['error_review_check'] = ' missing data inner ';
			echo json_encode($json_error);
		}
		
	}else{
			$json_error['error_review_check'] = ' missing data outer ';
			echo json_encode($json_error);
	}