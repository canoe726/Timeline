<?php
  require "db_require.php";
  $db = mysqli_connect($db_host,$db_user,$db_passwd,$db_name);

  if( !$db ) {
   die( 'MYSQL connect ERROR: ' . mysqli_error($db));
  }

  $user_id = "aaa";

  $sql = "SELECT COURSE.class_code, COURSE.class_time, COURSE.professor, COURSE.class_name
          FROM user_lists as USER, class_lists as COURSE, user_schedule as SCHEDULE
          WHERE USER.user_id = '$user_id' AND USER.user_id = SCHEDULE.user_id AND SCHEDULE.class_code = COURSE.class_code";
  $resource = mysqli_query($db, $sql);
  $result = array();

  if(!$resource) {
    die( 'MYSQL connect ERROR: ' . mysqli_error($db));
  }

  while($row = mysqli_fetch_array($resource)) {
    array_push($result, array("class_code"=>$row[0], "class_time"=>$row[1], "professor"=>$row[2], "class_name"=>$row[3]));
  }

  echo json_encode(array("result"=>$result), JSON_UNESCAPED_UNICODE);
  mysqli_close($db);
?>
