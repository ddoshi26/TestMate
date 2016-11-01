<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
 
<html lang="en">
<head>
  <title>Test Mate</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
  <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
  <script>PortNo="1234"</script>
  <style>
  ul{
    list-style-type: none;
    margin: 0;
    padding:0;
    overflow: hidden;
    background-color:#333;
  }
  li {
    float: left;
  }
  li a{
    display: block;
    color: white;
    text-align: centre;
    padding: 14px 25px;
    text-decoration: none;
  }

  li a:hover:not(.aactive){ 
    background-color: #666;
  }
  .active{
    background-color:#666;
  }


  </style>
</head>
<body>

<div class="jumbotron text-center">
  <h1>Test Mate</h1>
  <p>Welcome to the Automated Testing System</p>
</div>
  
<ul>
  <li><a class="active" href="main.html">Home Page</a></li>
  <li><a href= "inputForm.html" >New TestModule</a> </li>
  <li><a href="stop.html">Stop TestModule</a></li>
  <li><a href="Help.html">Help</a></li>

</ul> 
<div class="container">
  <div class="row">
   
       <div class="table-responsive">
    	<h2> Tests Running </h2>
    	<p> This table shows the current tests being run by the server and all the related information</p>
    	<table class="table">
    		<thread>
    			<tr>
    				<th>#</th>
    				<th>Test Module</th>
    				<th>Last Run Job Time</th>
    				<th>Total Tests Run</th>
    				<th>Tests Passed</th>	
            <th>Tests Failed</th>  
    			</tr>	
    		</thread>	
        <!--<c:forEach var="ob" items="${testJob}">-->
    		<tbody>
         
    			<tr>

          <td>abc</td>
          <!-- <td>${ob.testPassed}</td>-->
            <td><button class="detail" onClick="window.open('details.html');"><span class="icon">details</span></button></td>
    			</tr>	
       
       
    		</tbody>	
        <!--</c:forEach>-->
    	</table>	
    </div>	
  </div>
</div>

</body>
</html>

