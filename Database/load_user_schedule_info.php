<?php
  require "db_require.php";
  $db = mysqli_connect($db_host,$db_user,$db_passwd,$db_name);

  if( !$db ) {
   die( 'MYSQL connect ERROR: ' . mysqli_error($db));
  }

  $user_id = $_POST['userID'];

  $sql = "SELECT SCHEDULE.class_code, CLASS.class_schedule, CLASS.class_name, CLASS.professor
          FROM user_schedule as SCHEDULE, class_lists as CLASS
          WHERE SCHEDULE.user_id='$user_id' AND SCHEDULE.class_code = CLASS.class_code AND SCHEDULE.class_code = CLASS.class_code AND SCHEDULE.class_code = CLASS.class_code";
  $resource = mysqli_query($db, $sql);
  $result = array();

  if(!$resource) {
    die( 'MYSQL connect ERROR: ' . mysqli_error($db));
  }

  while($row = mysqli_fetch_array($resource)) {
    array_push($result,
              array("class_code"=>$row[0], "class_schedule"=>$row[1], "class_name"=>$row[2], "professor"=>$row[3]));
  }
  header('Content-Type: application/json; charset=utf8');
  $json = json_encode(array("result"=>$result), JSON_UNESCAPED_UNICODE);
  print_r($json);

  mysqli_close($db);
?>
