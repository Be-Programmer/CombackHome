# CombackHome


<img src="https://github.com/Be-Programmer/CombackHome/blob/develop/datas/log.png" width="400" height="300"> <br>

<br>

## Motivation
Make automatic alarm application which notify departure time to destination

## Our Function
<img src="https://github.com/Be-Programmer/CombackHome/blob/develop/datas/b1.png" width="40" height="40"> Set the dstination <br>
<img src="https://github.com/Be-Programmer/CombackHome/blob/develop/datas/b2.png" width="40" height="40"> Set the curfew time <br>
<img src="https://github.com/Be-Programmer/CombackHome/blob/develop/datas/b3.png" width="40" height="40"> Choice the alarm music among user’s phone <br>
<img src="https://github.com/Be-Programmer/CombackHome/blob/develop/datas/b4.png" width="40" height="40"> Show the subway route to arrive destination <br>


<br>
Show the user’s current location in Google map
Notify the nearest subway station and connect user’s position
Notify the nearest subway station and connect user’s position


## System Flow

  <img src="https://github.com/Be-Programmer/CombackHome/blob/develop/datas/system.png" width="600" height="450"> <br>
  
  
## Key Techniques

- Receive and split Subway data and store them in SQLite Database 
- Find shortest subway route by using Dijkstra algorithm.
- Compute depart time(alarm time) by considering distance and time table of subway
- Service component state should be changed along with the mode of application

## Screenshot

<table border="1" width="450">
  <tr height="20" width = "450">
  <th width = "150"> <h2> 1. Not set destination </th>
  <th width = "150"> <h2> 2. Set destination </th>
  <th width = "150"> <h2> 3. Complete to set destination  </th>
  </tr>
  <tr height = "200">  
  <td> <img src="https://github.com/Be-Programmer/CombackHome/blob/develop/datas/d1.png" width="100" height="200"> </td>
  <td> <img src="https://github.com/Be-Programmer/CombackHome/blob/develop/datas/d2.png" width="100" height="200"> </td>
  <td> <img src="https://github.com/Be-Programmer/CombackHome/blob/develop/datas/d3.png" width="100" height="200"> </td>
  </tr>
</table>

| 1. Not set destination | 2. Set destination | 3. Complete to set destination |
| :--:|:--:|:--:|
|<img src="https://github.com/Be-Programmer/CombackHome/blob/develop/datas/d1.png" width="100" height="200">| <img src="https://github.com/Be-Programmer/CombackHome/blob/develop/datas/d2.png" width="100" height="200">| <img src="https://github.com/Be-Programmer/CombackHome/blob/develop/datas/d3.png" width="100" height="200">|

<br>

| 4. Set curfew time| 5. Select alarm music | 6. Select alarm music |
| :--:|:--:|:--:|
|<img src="https://github.com/Be-Programmer/CombackHome/blob/develop/datas/d4.png" width="100" height="200">| <img src="https://github.com/Be-Programmer/CombackHome/blob/develop/datas/d5.png" width="100" height="200">| <img src="https://github.com/Be-Programmer/CombackHome/blob/develop/datas/d6.png" width="100" height="200">|

<br>


| 7. Show subway route| 8. Off the service | 
| :--:|:--:|
|<img src="https://github.com/Be-Programmer/CombackHome/blob/develop/datas/d7.png" width="100" height="200">| <img src="https://github.com/Be-Programmer/CombackHome/blob/develop/datas/d8.png" width="100" height="200">|

<br>







