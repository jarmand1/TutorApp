<?php
include_once 'connection.php';
	
	class User_Retrieve {
		
		private $db;
		private $connection;
		
		 function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
		
		public function get_user_values($email)
		{
			$query = "SELECT * FROM user WHERE email = '$email'";
			$result = mysqli_query($this->connection, $query);
			if(mysqli_num_rows($result)>0){
				while($row = mysqli_fetch_assoc($result)){
				$json['success_get_info'] = 'success';
				$json['email'] = $row['Email'];
				$json['first_name'] = $row['First_Name'];
				$json['last_name'] = $row['Last_Name'];
				$json['is_tutor'] = $row['Is_Tutor'];
				$is_tutor  = $row['Is_Tutor'];
				$json['phone_number'] = $row['Phone_Number'];
				
				}
			#Error, not returning these values Update:maybe good?
			
				if($is_tutor== 'Y'){		
					$query2 = "SELECT * FROM tutor WHERE Tutor_Email = '$email'";
					$result2 = mysqli_query($this->connection, $query2);
					if(mysqli_num_rows($result)>0){
						while($row = mysqli_fetch_assoc($result2)){
							$json['thirty_minute'] = $row['Thirty_Minute_Rate'];
							$json['sixty_minute'] = $row['Sixty_Minute_Rate'];
							$json['ram_id'] = '';
						}
					}
				}elseif($is_tutor== 'N'){
					$query2 = "SELECT * FROM student WHERE Student_Email = '$email'";
					$result2 = mysqli_query($this->connection, $query2);
					if(mysqli_num_rows($result)>0){
						while($row = mysqli_fetch_assoc($result2)){
							$json['ram_id'] = $row['Ram_ID'];
							$json['thirty_minute'] = '';
							$json['sixty_minute'] = '';
						}
					}
				}
				echo json_encode($json);
				mysqli_close($this -> connection);
			}else{
            
			$json['error_get_info'] = 'Bad';	
			echo json_encode($json);
			mysqli_close($this->connection);
			}
			
		}
		
	}
	
	
	$user = new User_Retrieve();
	if(isset($_POST['email'])) {
		$email = $_POST['email'];
		
		if(!empty($email)){
			$user-> get_user_values($email);
			
		}else{
			$json_error['error_get_info'] =  ' missing data';
			echo json_encode($json_error);
		}
		
	}
	


		