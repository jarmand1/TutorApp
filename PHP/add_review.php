<?php
include_once 'connection.php';
	
	class  User_Review {
		
		private $db;
		private $connection;
		
		 function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
		
		public function add_review($app_id, $review, $rating)
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
						$query3 = "INSERT INTO review (Appointment_ID, Review, Rating) VALUES ( '$app_id','$review', '$rating')";
						$result3 = mysqli_query($this->connection, $query3);
						$json['success_review_added'] = ' Added review';
					}else{
						$json['error_review'] = ' review for this appointment already exists';
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
	
	
	
	$user = new User_Review();
	if(isset($_POST['app_id']) && isset($_POST['review']) && isset($_POST['rating'])){
			$app_id = $_POST['app_id'];
			$review = $_POST['review'];
			$rating = $_POST['rating'];
			
		if(!empty($app_id) && !empty($review) && !empty($rating)){
			$user->add_review($app_id, $review, $rating);
		}else{
			$json_error['error_add_review'] = ' missing data inner ';
			echo json_encode($json_error);
		}
		
	}else{
			$json_error['error_add_review'] = ' missing data outer ';
			echo json_encode($json_error);
	}