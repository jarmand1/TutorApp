<?php
include_once 'connection.php';
	
	class  User_Requested_Appointment {
		
		private $db;
		private $connection;
		
		 function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
		
		public function update_appt_values($email,$course_id,$app_date,$app_begin,$app_end,$is_tutor, $building)
		{
			//Adding availability to tutor_availability table
			if($is_tutor == 'Y'){ //START IF
				$query = "SELECT Tutor_ID FROM tutor WHERE Tutor_Email='$email'";
				$result = mysqli_query($this->connection, $query);
				if(mysqli_num_rows($result)>0){
               	 	while($row = mysqli_fetch_assoc($result)){ //START WHILE
						$tutor_id = $row['Tutor_ID'];
						#$json['email'] = $row['Email'];
						} //END WHILE
						//checking for duplicate appointments
						$query2 = "SELECT Appointment_Date, Appointment_Begin, Appointment_End FROM tutor_availability WHERE Tutor_ID='$tutor_id' AND Appointment_Date ='$app_date' AND (Appointment_Begin='$app_begin' OR Appointment_End='$app_end')";
						$result2 = mysqli_query($this->connection, $query2); 
						while($row = mysqli_fetch_assoc($result2)){
							$newApp_date = ['Appointment_Date'];
							$newApp_begin = ['Appointment_Begin'];
							$newApp_end = ['Appointment_End'];
						}
						//if($newApp_date == $app_date && (($newApp_begin == $app_begin) || ($newApp_begin > $app_begin && $newApp_begin < $app_end))){
						  if(mysqli_num_rows($result2)>0){
							$json['insert_error_availability'] = ' duplicate tutor appointment';
						}else{
							$query3 = "INSERT INTO tutor_availability (Tutor_ID, Course_ID, Appointment_Date, Appointment_Begin, Appointment_End) VALUES ( '$tutor_id','$course_id', '$app_date','$app_begin','$app_end')";
							$result3 = mysqli_query($this->connection, $query3);
							$json['success_information_added_requested_appt'] = ' Added tutor availability for: '.$email;
						}
					echo json_encode($json);
					mysqli_close($this -> connection);
				}else{
            
				$json['error_requested_appt'] = ' query failed, user likely doesn\'t exist '.mysqli_error($this->connection);	
				echo json_encode($json);
				mysqli_close($this->connection);
				}
			}//END IF
			//TODO: Student requested_appointment table
			elseif($is_tutor == 'N'){ //START IF
				$query = "SELECT Ram_ID FROM student WHERE Student_Email='$email'";
				$result = mysqli_query($this->connection, $query);
				if(mysqli_num_rows($result)>0){
               	 	while($row = mysqli_fetch_assoc($result)){ //START WHILE
						$ram_id = $row['Ram_ID'];
						} //END WHILE
						//checking for duplicate appointments
						$query2 = "SELECT Appointment_Date, Appointment_Begin, Appointment_End FROM requested_appointment WHERE Ram_ID ='$ram_id' AND Appointment_Date ='$app_date' 
						AND Appointment_Begin='$app_begin' AND Appointment_End = '$app_end'";
						$result2 = mysqli_query($this->connection, $query2);
						while($row = mysqli_fetch_assoc($result2)){
							$newApp_date = ['Appointment_Date']; //not doing anything with this atm
							$newApp_begin = ['Appointment_Begin'];
							$newApp_end = ['Appointment_End'];
						}
						//if($newApp_date == $app_date && (($newApp_begin == $app_begin) || ($newApp_begin > $app_begin && $newApp_begin < $app_end))){
						  if(mysqli_num_rows($result2)>0){
							$json['insert_error_availability'] = ' duplicate student appointment';
						}else{
							$query3 = "INSERT INTO requested_appointment (Course_ID, Ram_ID, Appointment_Date, Appointment_Begin, Appointment_End, Building) VALUES ('$course_id','$ram_id','$app_date','$app_begin','$app_end','$building')";
							$result3 = mysqli_query($this->connection, $query3);
							$json['success_information_added_requested_appt'] = ' Added student availability for: '.$email;
						}
					echo json_encode($json);
					mysqli_close($this -> connection);
				}else{
					$json['error_requested_appt'] = ' query failed, user likely doesn\'t exist '.mysqli_error($this->connection);	
					echo json_encode($json);
					mysqli_close($this->connection);
				}

		}
	}	
}	
	
	
	$user = new User_Requested_Appointment();
	if(isset($_POST['email'], $_POST['course_id'], $_POST['app_date'], $_POST['app_begin'], $_POST['app_end'], $_POST['is_tutor'], $_POST['building'])){
			$email = $_POST['email'];
			$course_id = $_POST['course_id'];
        	$app_date = $_POST['app_date'];
        	$app_begin = $_POST['app_begin'];
        	$app_end = $_POST['app_end'];
			$is_tutor = $_POST['is_tutor'];
			$building = $_POST['building'];
		
		if(!empty($email) && !empty($course_id) && !empty($app_date) && !empty($app_begin) && !empty($app_end) && !empty($is_tutor) && !empty($building)){
			
			$user-> update_appt_values($email,$course_id,$app_date,$app_begin,$app_end,$is_tutor, $building);
			
		}else{
			$json_error['error_requested_appt'] = ' missing data inner ';
			echo json_encode($json_error);
		}
		
	}else{
			$json_error['error_requested_appt'] = ' missing data outer ';
			echo json_encode($json_error);
	}