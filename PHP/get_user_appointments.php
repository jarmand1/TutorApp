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
				//$json['success_get_info'] = 'success';
				$is_tutor = $row['Is_Tutor'];
					
				}
			#Error, not returning these values Update:maybe good?
				if(!isset($is_tutor)){
					$json = array(array("error_get_appointments" => "User is probably in account creation"));
				}
				else if($is_tutor== 'Y'){		
					$query2 = "SELECT Tutor_ID FROM tutor WHERE Tutor_Email = '$email'";
					$result2 = mysqli_query($this->connection, $query2);
					if(mysqli_num_rows($result2)>0){
						while($row = mysqli_fetch_assoc($result2)){
							$tutor_id = $row['Tutor_ID'];
						}
						//$query3 = "SELECT * FROM appointment WHERE Tutor_ID = '$tutor_id'";
					    //$query3 = "SELECT Appointment_ID, Course_ID, Ram_ID, Tutor_ID, Student_Appointment_ID, Tutor_Appointment_ID, Appointment_Date, Appointment_Begin, Appointment_End, 
						//(SELECT Student_Email FROM student WHERE a.Ram_ID = Ram_ID) as Student_Email FROM appointment a WHERE Tutor_ID = '$tutor_id'";
						$query3 = "SELECT Appointment_ID, a.Course_ID, c.Course_Description, a.Ram_ID, Tutor_ID, Student_Appointment_ID, Tutor_Appointment_ID, Appointment_Date, Appointment_Begin, Appointment_End,
						stu.First_Name, stu.Last_Name, stu.Student_Email, a.Building
						  FROM appointment a 
                          JOIN (SELECT Ram_ID, Student_Email, First_Name, Last_Name FROM student) as stu ON a.Ram_ID = stu.Ram_ID 
                          JOIN course c ON a.Course_ID = c.Course_ID
                          WHERE Tutor_ID ='$tutor_id'";
						$result3 = mysqli_query($this->connection, $query3);
						if(mysqli_num_rows($result3)>0){
						while($row = mysqli_fetch_assoc($result3)){
							$json[] = $row;
						}
					}else{
						$json = array(array("error_get_appointments" => "No appointments found"));
					}
				}
				}elseif($is_tutor== 'N'){
					$query2 = "SELECT Ram_ID FROM student WHERE Student_Email = '$email'";
					$result2 = mysqli_query($this->connection, $query2);
					if(mysqli_num_rows($result2)>0){
						while($row = mysqli_fetch_assoc($result2)){
							$ram_id = $row['Ram_ID'];
							
						}
						
						//$query3 = "SELECT * FROM appointment WHERE Ram_ID = '$ram_id'";
						//$query3 = "SELECT Appointment_ID, Course_ID, Ram_ID, Tutor_ID, Student_Appointment_ID, Tutor_Appointment_ID, Appointment_Date, Appointment_Begin, Appointment_End, 
						//(SELECT Tutor_Email FROM tutor WHERE a.Tutor_ID = Tutor_ID) as Tutor_Email FROM appointment a WHERE Ram_ID = '$ram_id'";
						//$query3 = "SELECT Appointment_ID, Course_ID, Ram_ID, Tutor_ID, Student_Appointment_ID, Tutor_Appointment_ID, a.Appointment_Date, a.Appointment_Begin, a.Appointment_End, i.Total,
						//(SELECT Tutor_Email FROM tutor WHERE a.Tutor_ID = Tutor_ID) as Tutor_Email  FROM appointment a
                        //INNER JOIN invoice i ON i.Invoice_ID = a.Appointment_ID
                        //WHERE Ram_ID = '$ram_id'";
						$query3 = "SELECT Appointment_ID, a.Course_ID, c.Course_Description, Ram_ID, tut.Tutor_ID, Student_Appointment_ID, Tutor_Appointment_ID, a.Appointment_Date, a.Appointment_Begin, a.Appointment_End, i.Total, 
						tut.First_Name, tut.Last_Name, tut.Tutor_Email, a.Building
					    FROM appointment a
                        INNER JOIN invoice i ON i.Invoice_ID = a.Appointment_ID
                        INNER JOIN (SELECT Tutor_ID, Tutor_Email, First_Name, Last_Name FROM tutor) as tut ON  a.Tutor_ID = tut.Tutor_ID
						INNER JOIN course c ON c.Course_ID = a.Course_ID
                        WHERE Ram_ID = '$ram_id'";
						$result3 = mysqli_query($this->connection, $query3);
						if(mysqli_num_rows($result3)>0){
						while($row = mysqli_fetch_assoc($result3)){
							$json[] = $row;
						}
					}else{
						$json = array(array("error_get_appointments" => "No appointments found"));
					}
				}
				}
				echo json_encode($json);
				mysqli_close($this -> connection);
			}else{
            
			$json = array(array("error_get_appointments" => "Bad"));	
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
			$json_error = array(array("error_get_info" =>  "missing data"));
			echo json_encode($json_error);
		}
		
	}else{
		$json_error = array(array("error_get_info" =>  "missing data"));
			echo json_encode($json_error);
	}
	


		