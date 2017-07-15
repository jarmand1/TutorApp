<?php
include_once 'connection.php';
	
	class Appointment {
		
		private $db;
		private $connection;
		
		 function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
		//we only need the email and the isTutor value because this script will use those values to search the respective 'waiting' tables to see if they have even made a requested appt
		//if they did, then we can search the opposite table to see if there's a match
		public function match_appointments($email,$is_tutor)
		{
			//tutor
			if($is_tutor == "Y"){ 
				$query = "SELECT Tutor_ID FROM tutor WHERE Tutor_Email='$email'";
				$result = mysqli_query($this->connection, $query);
				if(mysqli_num_rows($result)>0){ //START OUTER IF
					while($row = mysqli_fetch_assoc($result)){ //START WHILE
						$tutor_id = $row['Tutor_ID'];
					}
					$query2 = "SELECT * FROM tutor_availability WHERE Tutor_ID='$tutor_id'";
					$result2 = mysqli_query($this->connection, $query2);
					if(mysqli_num_rows($result2)>0){ //START INNER IF 1
						while($row = mysqli_fetch_assoc($result2)){ //START WHILE
							$tutor_appt_id = $row['Tutor_Appointment_ID'];
							$course_id = $row['Course_ID'];
							$app_date = $row['Appointment_Date'];
							$app_begin = $row['Appointment_Begin'];
							$app_end = $row['Appointment_End'];
							//main query to match all tutor availability with student availabilities.
							$query3 = "SELECT  r.Student_Appointment_ID, r.Ram_ID, r.Course_ID, course.Course_Description, r.Appointment_Date, r.Appointment_Begin, r.Appointment_End, r.Building 
							FROM requested_appointment  as r
							INNER JOIN course ON r.Course_ID = course.Course_ID
							WHERE r.Course_ID='$course_id' AND r.Appointment_Date = '$app_date' AND r.Appointment_Begin = '$app_begin' AND r.Appointment_End = '$app_end'";
							$result3 = mysqli_query($this->connection, $query3);
							if(mysqli_num_rows($result3)>0){ //START INNER IF 2
								while($row2 = mysqli_fetch_assoc($result3)){ //START WHILE
									$ram_id = $row2['Ram_ID'];
									//after the matches are retrieved, run another query to make sure there isn't an appointment during that time.
									$query4 = "SELECT count(*) as Count FROM appointment WHERE Appointment_Begin = '$app_begin' AND Appointment_End = '$app_end' AND Appointment_Date = '$app_date' AND (Ram_ID = '$ram_id' OR Tutor_ID = '$tutor_id')";
									//or-- $query4 = "SELECT count(*) as Count FROM appointment WHERETutor_Appointment_ID = '$tutor_appt_id';
									$result4 = mysqli_query($this->connection, $query4);
									while($row3 = mysqli_fetch_assoc($result4)){
										if($row3['Count'] == 0){
											$query5 = "SELECT * FROM student WHERE Ram_ID = '$ram_id'";
											$result5 = mysqli_query($this->connection, $query5);
											while($row4 = mysqli_fetch_assoc($result5)){
												$json['Tutor_Appointment_ID'] = $tutor_appt_id;
												$json['Student_Appointment_ID'] = $row2['Student_Appointment_ID'];
												$json['Ram_ID'] = $row2['Ram_ID'];
												$json['First_Name'] = $row4['First_Name'];
												$json['Last_Name'] = $row4['Last_Name'];
												$json['Student_Email'] = $row4['Student_Email'];
												$json['Course_ID'] = $row2['Course_ID'];
												$json['Course_Description'] = $row2['Course_Description'];
												$json['Appointment_Begin'] = $app_begin;
												$json['Appointment_End'] = $app_end;
												$json['Appointment_Date'] = $app_date;
												$json['Building'] = $row2['Building'];
												//$solutions[] = $row['Tutor_ID'];
												//$matches[] = $row2;
												$matches[] = $json;
											}
										}
									}		
										
								}

							}//END INNER IF 2			
						}//END WHILE
						if(!isset($matches)){
							$matches = array(array("error_match" => "match_not_found"));
						}
							echo json_encode($matches);
							mysqli_close($this->connection);
					}//END INNER IF 1
					else{
							//$json['failure_match_not_found'] = 'match not found';
							$json = array(array("failure_match_not_found" => "match not found"));
							echo json_encode($json);
							mysqli_close($this->connection);
						}
					}//END OUTER IF
					else{
						//$json['error_match'] = 'user likely does not exist';
						$json = array(array("failure_match" => "tutor likely does not exist"));
						echo json_encode($json);
						mysqli_close($this->connection);	
				}
			} 	
			elseif($is_tutor == "N"){  //student
				$query = "SELECT Ram_ID FROM student WHERE Student_Email='$email'";
				$result = mysqli_query($this->connection, $query);
				if(mysqli_num_rows($result)>0){ //START OUTER IF
					while($row = mysqli_fetch_assoc($result)){ //START WHILE
						$ram_id = $row['Ram_ID'];
					}
					$query2 = "SELECT * FROM requested_appointment WHERE Ram_ID='$ram_id'";
					$result2 = mysqli_query($this->connection, $query2);
					if(mysqli_num_rows($result2)>0){ //START INNER IF 1
						while($row = mysqli_fetch_assoc($result2)){ //START OUTER WHILE
							$stu_appt_id = $row['Student_Appointment_ID'];
							$course_id = $row['Course_ID'];
							$app_date = $row['Appointment_Date'];
							$app_begin = $row['Appointment_Begin'];
							$app_end = $row['Appointment_End'];		
							$query3 = "SELECT Tutor_Appointment_ID, t.Tutor_ID, t.Course_ID, course.Course_Description, t.Appointment_Date, t.Appointment_Begin, 
							t.Appointment_End
							FROM tutor_availability as t
							INNER JOIN course on t.Course_ID = course.Course_ID
							WHERE t.Course_ID='$course_id' AND t.Appointment_Date = '$app_date' AND t.Appointment_Begin = '$app_begin' AND t.Appointment_End = '$app_end'";
							$result3 = mysqli_query($this->connection, $query3);		
							if(mysqli_num_rows($result3)>0){ //START INNER IF 2
							while($row2 = mysqli_fetch_assoc($result3)){ //START WHILE
								$tutor_id = $row2['Tutor_ID'];
								$query4 = "SELECT count(*) as Count FROM appointment WHERE Appointment_Begin = '$app_begin' AND Appointment_End = '$app_end' AND Appointment_Date = '$app_date' AND (Ram_ID = '$ram_id' OR Tutor_ID = '$tutor_id')";
								$result4 = mysqli_query($this->connection, $query4);
								while($row3 = mysqli_fetch_assoc($result4)){
										if($row3['Count'] == 0){
											$query5 = "SELECT * FROM tutor WHERE Tutor_ID = '$tutor_id'";
											$result5 = mysqli_query($this->connection, $query5);
											while($row4 = mysqli_fetch_assoc($result5)){
												$json['Tutor_Appointment_ID'] = $row2['Tutor_Appointment_ID'];
												$json['Student_Appointment_ID'] = $stu_appt_id;
												$json['Tutor_ID'] = $row2['Tutor_ID'];
												$json['First_Name'] = $row4['First_Name'];
												$json['Last_Name'] = $row4['Last_Name'];
												$json['Thirty_Minute_Rate'] = $row4['Thirty_Minute_Rate'];
												$json['Sixty_Minute_Rate'] = $row4['Sixty_Minute_Rate'];						
												$json['Student_Email'] = $row4['Tutor_Email'];
												$json['Course_ID'] = $row2['Course_ID'];
												$json['Course_Description'] = $row2['Course_Description'];
												$json['Appointment_Begin'] = $app_begin;
												$json['Appointment_End'] = $app_end;
												$json['Appointment_Date'] = $app_date;
												$matches[] = $json;
											}
											
										}
								}
							}//END WHILE
						}//END INNER IF 2								
					}//END OUTER WHILE
						if(!isset($matches)){
							$matches = array(array("error_match" => "match_not_found"));
						}
							echo json_encode($matches);
							mysqli_close($this->connection);
					}//END INNER IF 1
					else{
							//$json['failure_match_not_found'] = 'match not found';
							$json = array(array("failure_match_not_found" => "match not found"));
							echo json_encode($json);
							mysqli_close($this->connection);
						}
					}
					else{
						$json['error_match'] = 'user likely does not exist';
						$json = array(array("failure_match" => "student likely does not exist"));
						echo json_encode($json);
						mysqli_close($this->connection);
					}	
				} //END OUTER IF
				else{
					$json['error_match'] = 'user likely does not exist';
					$json = array(array("failure_match" => "user likely does not exist"));
					echo json_encode($json);
					mysqli_close($this->connection);
				}
			//if(empty($json)){
			//	$json = array(array("failure_match" => "user likely does not exist"));
				//	echo json_encode($json);
					//mysqli_close($this->connection);
		//	}
			
		}
		
			
	}
		
	
	
	
	$user = new Appointment();
	if(isset($_POST['email'],$_POST['is_tutor'])){
			$email = $_POST['email'];
        	$is_tutor = $_POST['is_tutor'];
		
		if(!empty($email) && !empty($is_tutor)){
			
			$user-> match_appointments($email,$is_tutor);
			
		}else{
			$json_error['error_match'] = ' missing data inner ';
			echo json_encode($json_error);
		}
		
	}else{
			$json_error['error_match'] = ' missing data outer ';
			echo json_encode($json_error);
	}