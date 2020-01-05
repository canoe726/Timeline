<?php
  require "db_require.php";
  $db = mysqli_connect($db_host,$db_user,$db_passwd,$db_name);

  if( !$db ) {
   die( 'MYSQL connect ERROR: ' . mysqli_error($db));
  }

  $user_id = $_POST['userID'];
  $class_code = $_POST['classCODE'];

  $sql = "DELETE FROM user_schedule WHERE user_id='$user_id' AND class_code='$class_code'";

  $resource = mysqli_query($db, $sql);

  if(!$resource) {
    die( 'MYSQL connect ERROR: ' . mysqli_error($db));
  }

  mysqli_close($db);
?>
