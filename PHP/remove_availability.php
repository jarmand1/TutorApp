<?php
include_once 'connection.php';
	
	class User_Delete{
		
		private $db;
		private $connection;
		
		 function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
		
		public function delete_availability($is_tutor, $app_id)
		{
				if($is_tutor == 'Y'){		
					$query = "SELECT * FROM tutor_availability WHERE Tutor_Appointment_ID = '$app_id'";
					$result = mysqli_query($this->connection, $query);
					if(mysqli_num_rows($result)>0){
						$query2 = "DELETE FROM tutor_availability WHERE Tutor_Appointment_ID = '$app_id'";
						$result2 = mysqli_query($this->connection, $query2);
						$json['delete_availability_success'] = ' Availability deleted';
					}else{
						$json['delete_availability_error'] = ' Availability does not exist';
					}
				}elseif($is_tutor == 'N'){
					$query = "SELECT * FROM requested_appointment WHERE Student_Appointment_ID = '$app_id'";
					$result = mysqli_query($this->connection, $query);
					if(mysqli_num_rows($result)>0){
						$query2 = "DELETE FROM requested_appointment WHERE Student_Appointment_ID = '$app_id'";
						$result2 = mysqli_query($this->connection, $query2);
						$json['delete_availability_success'] = ' Availability deleted';
					}else{
						$json['delete_availability_error'] = ' Availability does not exist';
					}
					
				}
				echo json_encode($json);
				mysqli_close($this -> connection);
			
			
		}
		
	}
	
	
	$user = new User_Delete();
	if(isset($_POST['is_tutor'],$_POST['app_id'])) {
		$is_tutor = $_POST['is_tutor'];
		$app_id = $_POST['app_id'];
		
		if(!empty($is_tutor) && !empty($app_id)){
			$user-> delete_availability($is_tutor, $app_id);
			
		}else{
			$json_error['error_get_info'] =  ' missing data';
			echo json_encode($json_error);
		}
		
	}
	


		