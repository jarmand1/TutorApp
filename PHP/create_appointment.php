<?php
include_once 'connection.php';
	
	class CreateAppointment { //START CLASS
		 
		private $db;
		private $connection;
		
		 function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
	
	//this function takes the availability ids for tutors and students and creates an appointment based on their data
	//Since both need to be not null to work, the appointment data is obtained using the tutor_app_id and then the ram id is retrieved using the stu_app_id (this can be reversed, will not change outcome)
		public function create_appointment($stu_app_id,$tutor_app_id)
		{//START FUNCTION
				
				$query = "SELECT * FROM tutor_availability WHERE Tutor_Appointment_ID='$tutor_app_id'";
				$result = mysqli_query($this->connection, $query);
				if(mysqli_num_rows($result)>0){ //START OUTER IF
					while($row = mysqli_fetch_assoc($result)){ //START WHILE
						$tutor_id = $row['Tutor_ID'];
						$course_id = $row['Course_ID'];
						$app_date = $row['Appointment_Date'];
						$app_begin = $row['Appointment_Begin'];
						$app_end = $row['Appointment_End'];
					}//END WHILE
					$query2 = "SELECT * FROM requested_appointment WHERE Student_Appointment_ID='$stu_app_id'";
					$result2 = mysqli_query($this->connection, $query2);
					if(mysqli_num_rows($result2)>0){ //START INNER IF 1
						while($row = mysqli_fetch_assoc($result2)){ //START WHILE 
							$ram_id = $row['Ram_ID'];
							$building = $row['Building'];
						}//END WHILE 
						
						//check to see if appointment exists already
						$exists = 'FALSE';
						$query3 = "SELECT * FROM appointment WHERE Ram_ID = '$ram_id'";
						$result3 = mysqli_query($this->connection, $query3);
						if(mysqli_num_rows($result3)>0){//START INNER IF 2
							while($row = mysqli_fetch_assoc($result3)){//START WHILE
								if(($row['Appointment_Date'] == $app_date) && ($row['Appointment_Begin'] == $app_begin) && ($row['Appointment_End'] == $app_end)){
									$exists = 'TRUE';
								}
							}//END WHILE
							if($exists == 'TRUE'){
									$json['insert_error'] = 'appointment already exists'; //this will return if its theres an appointment already during that time, maybe make it more descriptive (same tutor, same subject...)
									
									}else{
										$query4 = "INSERT INTO appointment (Course_ID, Ram_ID, Tutor_ID, Student_Appointment_ID, Tutor_Appointment_ID, Appointment_Date, Appointment_Begin, Appointment_End, Building)
										 VALUES ('$course_id','$ram_id','$tutor_id', '$stu_app_id', '$tutor_app_id', '$app_date','$app_begin','$app_end','$building')";
										$result4 = mysqli_query($this->connection, $query4);
										if(mysqli_affected_rows($this->connection)>0){ //START INNER IF 2
											$json['appointment_info'] = ' inserted 48 ';
											$query5 = "SELECT Appointment_ID FROM appointment WHERE Course_ID = '$course_id' AND Ram_ID = '$ram_id' AND Tutor_ID = '$tutor_id' AND Student_Appointment_ID = '$stu_app_id' 
											AND Tutor_Appointment_ID = '$tutor_app_id' AND Appointment_Date = '$app_date' AND Appointment_Begin = '$app_begin' AND Appointment_End = '$app_end'";
											$result5 = mysqli_query($this->connection, $query5);
											while($row2 = mysqli_fetch_assoc($result5)){
												$app_id = $row2['Appointment_ID'];
												$time = ($app_end - $app_begin);
												$query6 = "SELECT * FROM tutor WHERE Tutor_ID = $tutor_id";
												$result6 = mysqli_query($this->connection, $query6);
												while($row3 = mysqli_fetch_assoc($result6)){
													if($time == '0100'){
														$total = $row3['Sixty_Minute_Rate'];
													}elseif($time == '0030'){
														$total = $row3['Thirty_Minute_Rate'];
													}
													$query7 = "INSERT INTO invoice (Invoice_ID, Appointment_Date, Appointment_Begin, Appointment_End, Total) VALUES ('$app_id', '$app_date', '$app_begin', '$app_end', '$total')";
													$result7 = mysqli_query($this->connection, $query7);
													if(mysqli_affected_rows($this->connection)>0){ //START INNER IF 2
														$json['invoice_info'] = ' inserted 74 ';
													}
												}
											}
										}
										else{
											$json['insert_error 52'] = mysqli_error($this->connection);
										}
									}//END ELSE
								
							}//END INNER IF 2
							else{
								$query4 = "INSERT INTO appointment (Course_ID, Ram_ID, Tutor_ID, Student_Appointment_ID, Tutor_Appointment_ID, Appointment_Date, Appointment_Begin, Appointment_End, Building) 
								VALUES ('$course_id','$ram_id','$tutor_id',  '$stu_app_id', '$tutor_app_id', '$app_date','$app_begin','$app_end', '$building')";
								$result4 = mysqli_query($this->connection, $query4);
								if(mysqli_affected_rows($this->connection)>0){ //START INNER IF 2
									$json['appointment_info'] = ' inserted 90 ';
									$query5 = "SELECT Appointment_ID FROM appointment WHERE Course_ID = '$course_id' AND Ram_ID = '$ram_id' AND Tutor_ID = '$tutor_id' AND Student_Appointment_ID = '$stu_app_id' 
											AND Tutor_Appointment_ID = '$tutor_app_id' AND Appointment_Date = '$app_date' AND Appointment_Begin = '$app_begin' AND Appointment_End = '$app_end'";
											$result5 = mysqli_query($this->connection, $query5);
											while($row2 = mysqli_fetch_assoc($result5)){
												$app_id = $row2['Appointment_ID'];
												$time = ($app_end - $app_begin);
												$query6 = "SELECT * FROM tutor WHERE Tutor_ID = '$tutor_id'";
												$result6 = mysqli_query($this->connection, $query6);
												while($row3 = mysqli_fetch_assoc($result6)){													
													if($time == 100){	
														$total = $row3['Sixty_Minute_Rate'];
													}elseif($time == 30){
														$total = $row3['Thirty_Minute_Rate'];
													}
													
													$query7 = "INSERT INTO invoice (Invoice_ID, Appointment_Date, Appointment_Begin, Appointment_End, Total) VALUES ('$app_id', '$app_date', '$app_begin', '$app_end', '$total')";
													$result7 = mysqli_query($this->connection, $query7);
													if(mysqli_affected_rows($this->connection)>0){ 
														$json['invoice_info'] = ' inserted 108 ';
													}
												}
											}
									}	//END INNER IF 2	
									
							}    //END ELSE
						
						}    //END INNER IF 1
						else{
							$json = array(array("error" => "can't find RAM id"));
						}
							echo json_encode($json);
							mysqli_close($this->connection);
					}//END OUTER IF 
					else{
							$json['failure_match_not_found'] = 'match not found';
							echo json_encode($json);
							mysqli_close($this->connection);
						}
		} //END FUNCTION
			
	} //END CLASS
		
			
			
	
	
	
	$user = new CreateAppointment();
	if(isset($_POST['stu_app_id'],$_POST['tutor_app_id'])){
        	$stu_app_id = $_POST['stu_app_id'];
			$tutor_app_id = $_POST['tutor_app_id'];
		
		if(!empty($stu_app_id) && !empty($tutor_app_id)){
			$user-> create_appointment($stu_app_id,$tutor_app_id);	
		}else{
			$json_error['error_match'] = ' POST value empty ';
			echo json_encode($json_error);
		}
		
	}else{
			$json_error['error_match'] = ' missing POST variables ';
			echo json_encode($json_error);
	}
	