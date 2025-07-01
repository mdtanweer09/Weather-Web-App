
  
  document.addEventListener("DOMContentLoaded", function () {
      var weatherIcon = document.getElementById("weather-icon");
      var valElement = document.getElementById("wc");

      if (!valElement) {
          console.log("Weather condition element not found.");
          return;
      }

      var val = valElement.value.trim().toLowerCase();
      

      switch (val) {
          case 'clouds':
              weatherIcon.src = "images/Clouds.png";
              break;
          case 'clear':
              weatherIcon.src = "images/sun.png";
              break;
          case 'rain':
              weatherIcon.src = "images/rain.png";
              break;
          case 'mist':
              weatherIcon.src = "images/mist.png";
              break;
          case 'snow':
              weatherIcon.src = "images/snow.png";
              break;
          case 'haze':
              weatherIcon.src = "images/haze.png";
              break;
          
      }
  });