<?php
  require "db_require.php";
  $db = mysqli_connect($db_host,$db_user,$db_passwd,$db_name);

  $user_id = $_POST['userID'];

  if( !$db ) {
   die( 'MYSQL connect ERROR: ' . mysqli_error($db));
  }

  $sql = "SELECT * FROM user_lists WHERE user_id='$user_id'";
  $resource = mysqli_query($db, $sql);

  $row = mysqli_fetch_assoc($resource);

  $user_id = $row['user_id'];

  $row_array = array("user_id" => $user_id);

  header('Content-Type: application/json; charset=utf8');
  $json = json_encode($row_array);

  print_r($json);

  mysqli_close($db);
?>
