<?php
  require "db_require.php";
  $db = mysqli_connect($db_host,$db_user,$db_passwd,$db_name);

  $user_id = $_POST['userID'];
  $user_pw = $_POST['userPW'];

  if( !$db ) {
   die( 'MYSQL connect ERROR: ' . mysqli_error($db));
  }

  $sql = "SELECT * FROM user_lists WHERE user_id='$user_id'";
  $resource = mysqli_query($db, $sql);

  $row = mysqli_fetch_assoc($resource);

  $user_id = $row['user_id'];
  $user_pw = $row['user_pw'];

  $row_array = array(
    "user_id" => $user_id,
    "user_pw" => $user_pw
  );

  header('Content-Type: application/json; charset=utf8');
  $json = json_encode($row_array);

  print_r($json);

  mysqli_close($db);
?>
