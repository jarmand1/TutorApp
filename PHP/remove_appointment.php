<?php
include_once 'connection.php';
	
	class User_Delete{
		
		private $db;
		private $connection;
		
		 function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
		
		public function delete_appointment($app_id)
		{
						
			$query = "SELECT * FROM appointment WHERE Appointment_ID = '$app_id'";
			$result = mysqli_query($this->connection, $query);
			if(mysqli_num_rows($result)>0){
				$query2 = "DELETE FROM appointment WHERE Appointment_ID = '$app_id'";
				$result2 = mysqli_query($this->connection, $query2);
				$json['delete_appointment_success'] = ' Appointment deleted';
				$query3 = "DELETE FROM invoice WHERE Invoice_ID = '$app_id'";
				$result3 = mysqli_query($this->connection, $query3);
				
			}else{
				$json['delete_appointment_error'] = ' Availability does not exist line 24';
				}
				echo json_encode($json);
				mysqli_close($this -> connection);
			
			
		}
		
	}
	
	
	$user = new User_Delete();
	if(isset($_POST['app_id'])) {
		$app_id = $_POST['app_id'];
		
		if(!empty($app_id)){
			$user-> delete_appointment($app_id);
			
		}else{
			$json_error['error_delete_apo'] =  ' missing data line 44';
			echo json_encode($json_error);
		}
		
	}
	


		