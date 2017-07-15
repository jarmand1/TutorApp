<?php
include_once 'connection.php';
	
	class User {
		
		private $db;
		private $connection;
		
		 function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
		
		public function does_user_exist($email,$password)
		{
			$query = "SELECT * FROM user WHERE Email='$email'";
			$result = mysqli_query($this->connection, $query);
			if(mysqli_num_rows($result)>0){
				$query2 = "SELECT * FROM user WHERE Email = '$email' AND Password = '$password'";
				$result2 = mysqli_query($this->connection, $query2);
				if(mysqli_num_rows($result2)>0){
					$json['success_log_in'] = ' Welcome '.$email;
				}else{
					$json['error_log_in_password'] = ' Incorrect Password '.$email;
				}
				
			}else{
				$json['error_log_in'] = 'Account does not exist';
			
				
			}
			echo json_encode($json);
			mysqli_close($this->connection);
		}
		
	}
	
	
	$user = new User();
	if(isset($_POST['email'],$_POST['password'])) {
		$email = $_POST['email'];
		$password = $_POST['password'];
		
		if(!empty($email) && !empty($password)){
			
			$encrypted_password = md5($password);
			$user-> does_user_exist($email,$encrypted_password);
			
		}else{
			$jsonerror['error'] = ' All fields required ';
			echo json_encode($jsonerror);
		}
	
	}else{
	$jsonerror['error'] = ' Post empty ';
		echo json_encode($jsonerror);
	}
