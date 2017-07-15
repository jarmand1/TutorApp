<?php
include_once 'connection.php';
	error_reporting(E_ALL);
ini_set('display_errors', 1);
	class User_Sign_Up {
		
		private $db;
		private $connection;
		
		 function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
		
		public function update_user_values($email, $password, $first_name, $last_name ,$is_tutor, $phone_number, $ram_id, $thirty_minute, $sixty_minute)
		{
			#IMPORTANT: these values need to be added to the respective tutor/students tables.  
			#1. Based on isTutor, add to tutor or student table in addition to users table
			#2. If the user is updating already existing values, (Run a query to determine if the user is already in the student/tutor tables) remove them from their current table 
			# and add to the new table.

			//first check to see if they exist
			$query0 = "SELECT * FROM user WHERE Email = '$email'";
			$result0 = mysqli_query($this->connection, $query0);
			if(mysqli_num_rows($result0)==0){ //START OUTER IF
			//	if(mysqli_affected_rows($this->connection)>0){ //START INNER IF
				//	$json['success_user_added'] = ' user created';
					if($is_tutor== 'Y'){
						$query2 = "INSERT INTO tutor (First_Name, Last_Name, Tutor_Email, Tutor_Phone_Number, Thirty_Minute_Rate, Sixty_Minute_Rate) VALUES ('$first_name', '$last_name', '$email', '$phone_number', $thirty_minute, $sixty_minute)";
						$result2 = mysqli_query($this->connection, $query2);
						if(mysqli_affected_rows($this->connection)>0){
							$query = "INSERT INTO user (Email, Password, First_Name, Last_Name, Is_Tutor, Phone_Number) VALUES ('$email', '$password', '$first_name', '$last_name', '$is_tutor', '$phone_number')";
							$result = mysqli_query($this->connection, $query);
							$json['success_user_added'] = ' tutor created'.__LINE__;
						}else{
							$json['error_user_added'] = ' tutor not created'.__LINE__;
						}
					}elseif($is_tutor == 'N'){
						$query2 = "INSERT INTO student (Ram_ID, First_Name, Last_Name, Student_Email, Student_Phone_Number) VALUES ('$ram_id','$first_name','$last_name','$email','$phone_number')";
						$result2 = mysqli_query($this->connection, $query2);
						if(mysqli_affected_rows($this->connection)>0){
							$query = "INSERT INTO user (Email, Password, First_Name, Last_Name, Is_Tutor, Phone_Number) VALUES ('$email', '$password', '$first_name', '$last_name', '$is_tutor', '$phone_number')";
							$result = mysqli_query($this->connection, $query);
							$json['success_user_added'] = ' student created'.__LINE__;
						}else{
							$json['error_user_added'] = ' student not created'.__LINE__;
						}
					}
			//	} //END INNER IF
				else{
					$json['error_user_added'] = ' user not added '.__LINE__;
				}
				echo json_encode($json);
				mysqli_close($this->connection);
			}//END OUTER IF
			else{ //START OUTER ELSE
			//This block occurs when a user already exists and wnats to update their update_user_values
			//first check to see if they exist (they should if we're here) and retrieve the values
				$query0 = "SELECT * FROM user WHERE Email='$email'";
				$result0 = mysqli_query($this->connection, $query0);
				if(mysqli_num_rows($result0)>0){ //START OUTER IF
					while($row = mysqli_fetch_assoc($result0)){
							$originalFName = $row['First_Name'];
							$originalLName = $row['Last_Name'];
							$originalIsTutor = $row['Is_Tutor'];
							$originalPhone = $row['Phone_Number'];			
					}//match their new values against the original
					if($originalFName != $first_name || $originalLName != $last_name || $originalIsTutor != $is_tutor || $originalPhone != $phone_number){ //START DATA CHECK	
						$noNewDataOuter = 'false';
					}///END DATA CHECK
					else{	
						$noNewDataOuter = 'true';
					}
					if($is_tutor == 'Y'){ 
						//if nothing is different, we still need to check the tutor/student tables to see if they changed their Ram or rate values
						$query1 = "SELECT * FROM tutor WHERE Tutor_Email='$email'";
						$result1 = mysqli_query($this->connection, $query1);
						if(mysqli_num_rows($result1)>0){
							while($row1 = mysqli_fetch_assoc($result1)){
								$originalThirtyMin = $row1['Thirty_Minute_Rate'];
								$originalSixtyMin = $row1['Sixty_Minute_Rate'];
								if($originalThirtyMin == $thirty_minute && $originalSixtyMin == $sixty_minute){
									$noNewData = 'true';								
								}else{
									$noNewData = 'false';					
								}
							}
						}else{
							$noNewData = 'false';
						}
					}elseif($is_tutor == 'N'){
						$query1 = "SELECT * FROM student WHERE Student_Email='$email'";
						$result1 = mysqli_query($this->connection, $query1);
						if(mysqli_num_rows($result1)>0){
							while($row1 = mysqli_fetch_assoc($result1)){
								$originalRam = $row1['Ram_ID'];
								if($originalRam == $ram_id){
									$noNewData = 'true';		
								}else{
									$noNewData = 'false';
								}	
							}
						}else{
							$noNewData = 'false';
						}
							
					}					
				}//END OUTER IF
				else{
					//shouldn't ever get here
					$json['fatal_error'] = ' fatal error '.__LINE__;
				}
				if($noNewData == 'false' || $noNewDataOuter == 'false'){
					$query = "UPDATE user SET First_Name = '$first_name', Last_Name = '$last_name', Is_Tutor ='$is_tutor', Phone_Number = '$phone_number' WHERE Email='$email'";
					$result = mysqli_query($this->connection, $query);
                	//$query2 = "SELECT * FROM user WHERE Email='$email'";
					//$json['success_information_added'] = ' Added user information for: '.__LINE__.$email;
					#if is_tutor = Y, 1.) we must check the student database to see if they already exist as a student.  If so, delete that tuple and add it into the tutor database.
					#2.) Check to see if the tuple exists in the tutor database, if it does, UPDATE, else, INSERT.
					if($is_tutor == 'Y' && $thirty_minute != '' && $sixty_minute != ''){//adding as a tutor
						$query3 = "SELECT * FROM student WHERE Student_Email = '$email'";
						$result3 = mysqli_query($this->connection, $query3);
						if(mysqli_num_rows($result3)>0){ //If tutor exists as a student
							$query4 = "DELETE FROM student WHERE Student_Email = '$email'";
							$result4 = mysqli_query($this->connection, $query4);
						}	
						$query5 = "SELECT * FROM tutor WHERE Tutor_Email = '$email'";
						$result5 = mysqli_query($this->connection, $query5);
						if(mysqli_num_rows($result5)>0){	//if tutor already exists
							$query6 = "UPDATE tutor SET First_Name = '$first_name', Last_Name = '$last_name', Tutor_Phone_Number = '$phone_number', Thirty_Minute_Rate =$thirty_minute, Sixty_Minute_Rate=$sixty_minute WHERE Tutor_Email = '$email'";
							$result6 = mysqli_query($this->connection, $query6);
							if(mysqli_affected_rows($this->connection)>0){
								$json['tutor_info_update_success'] = ' updated '.__LINE__;
							}else{
								$json['tutor_info_update_error'] = 'update failed'.__LINE__ .mysqli_error($this->connection);
							}
						
					}else{ //tutor doesn't exist
						$query6 = "INSERT INTO tutor (First_Name, Last_Name, Tutor_Email, Tutor_Phone_Number, Thirty_Minute_Rate, Sixty_Minute_Rate) VALUES ('$first_name','$last_name','$email','$phone_number',$thirty_minute,$sixty_minute)";
						$result6 = mysqli_query($this->connection, $query6);
						if(mysqli_affected_rows($this->connection)>0){
							$json['tutor_info_success'] = ' inserted '. __LINE__;
						}else{
							$json['tutor_info_error'] = 'insert failed' .__LINE__ .mysqli_error($this->connection);
						}
					}
				
			//STUDENT ADD
			}elseif($is_tutor == 'N' && $ram_id != ''){//adding as a student
					#if is_tutor = N, 1.) we must check the tutor database to see if they already exist as a tutor.  If so, delete that tuple and add it into the student database.
					#2.) Check to see if the tuple exists in the student database, if it does, UPDATE, else, INSERT.
					$query1 = "SELECT * FROM student WHERE Ram_ID = '$ram_id'";
					$result1 = mysqli_query($this->connection, $query1);
					if(mysqli_num_rows($result1)==0 || $originalIsTutor == $is_tutor){
						$query3 = "SELECT * FROM tutor WHERE Tutor_Email = '$email'";
						$result3 = mysqli_query($this->connection, $query3);
						if(mysqli_num_rows($result3)>0){ //If tutor exists as a student
							$query4 = "DELETE FROM tutor WHERE Tutor_Email = '$email'";
							$result4 = mysqli_query($this->connection, $query4);
						}
						$query5 = "SELECT * FROM student WHERE Student_Email = '$email'";
						$result5 = mysqli_query($this->connection, $query5);
						if(mysqli_num_rows($result5)>0){	//if student already exists
							$query6 = "UPDATE student SET Ram_ID = '$ram_id', First_Name = '$first_name', Last_Name = '$last_name', Student_Phone_Number = '$phone_number'  WHERE Student_Email = '$email'";
							$result6 = mysqli_query($this->connection, $query6);
							if(mysqli_affected_rows($this->connection)>0){
								$json['student_info_update_success'] = ' updated '. __LINE__;
							}else{
								$json['student_info_update_error'] = 'update failed ln ' .__LINE__ .mysqli_error($this->connection);
							}
						}else{ //student doesn't exist
							$query6 = "INSERT INTO student (Ram_ID, First_Name, Last_Name, Student_Email, Student_Phone_Number) VALUES ('$ram_id','$first_name','$last_name','$email','$phone_number')";
							$result6 = mysqli_query($this->connection, $query6);
							if(mysqli_affected_rows($this->connection)>0){
								$json['student_info_success'] = ' inserted '. __LINE__;
							}else{
								$json['student_info_error'] = 'insert failed '. __LINE__  .mysqli_error($this->connection);
							}
						}
					}else{
							$json['student_info_error'] = ' Ram_ID already exists '.__LINE__;	
					}
				}else{
						$json['student_info'] = 'error: missing information'.__LINE__;	
				}
	
				echo json_encode($json);
				mysqli_close($this -> connection);
			}else{
            
				$json['error_update'] = ' information is the same '.__LINE__;	
				echo json_encode($json);
				mysqli_close($this->connection);
			}
			
		} //END OUTER ELSE
		
	 }	
	
	}	
	
	
	$user = new User_Sign_Up();
	if(isset($_POST['email'],$_POST['password'],$_POST['first_name'],$_POST['last_name'],$_POST['is_tutor'],$_POST['phone_number'])){
			$email = $_POST['email'];
			$password = $_POST['password'];
			$encryptedPassword = md5($password);
			$first_name = $_POST['first_name'];
        	$last_name = $_POST['last_name'];
        	$is_tutor = $_POST['is_tutor'];
        	$phone_number = $_POST['phone_number'];
		if($_POST['is_tutor'] == 'Y'){
			if(isset($_POST['thirty_minute']) && isset($_POST['sixty_minute'])){
				$thirty_minute = $_POST['thirty_minute'];
				$sixty_minute = $_POST['sixty_minute'];
				$ram_id = 'empty';
			}else{
				$json_error['error_sign_up'] = ' missing tutor data ';
				echo json_encode($json_error);
			}
		}elseif($_POST['is_tutor'] == 'N'){
			if(isset($_POST['ram_id'])){
				$ram_id = $_POST['ram_id'];
				$thirty_minute = 0;
				$sixty_minute = 0;
			}else{
				$json_error['error_sign_up'] = ' missing student data ';
				echo json_encode($json_error);
			}
		}
		
		if(!empty($email) && !empty($encryptedPassword) && !empty($first_name) && !empty($last_name) && !empty($is_tutor) && !empty($phone_number) &&(!empty($ram_id) || (!empty($thirty_minute) && !empty($sixty_minute)))){
			
			$user-> update_user_values($email,$encryptedPassword, $first_name,$last_name,$is_tutor,$phone_number,$ram_id,$thirty_minute,$sixty_minute);
			
		}else{
			$json_error['error_sign_up'] = ' missing data inner ';
			echo json_encode($json_error);
		}
		
	}else{
			$json_error['error_sign_up'] = ' missing data outer ';
			echo json_encode($json_error);
	}