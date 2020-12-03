<?php
$con=mysqli_connect('localhost','root','');
mysqli_select_db($con,'labmaster');
$id=$_POST['id_id'];
$reject=$_POST['id_reject'];
$s1="update request set reject_reason='$reject',status='Rejected' where id='$id';";
$result1=mysqli_query($con,$s1);
if($result1)
{
	echo 'Success';
}
else
{
	echo 'Fail';
}
?>
