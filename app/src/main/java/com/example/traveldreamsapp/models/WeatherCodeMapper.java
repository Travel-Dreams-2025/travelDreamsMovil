// src/main/java/com/example/traveldreamsapp/models/WeatherCodeMapper.java

package com.example.traveldreamsapp.models;

import com.example.traveldreamsapp.R;

public class WeatherCodeMapper {

    public static int getWeatherIconResId(Integer weatherCode) {
        if (weatherCode == null) {
            return R.drawable.ic_clima_desconocido;
        }

        switch (weatherCode) {
            // Mapeo de drawables de imagenes .jpg:
            case 1000: // Despejado, Soleado
                return R.drawable.ic_clima_soleado; // Este era ic_clima_soleado.xml
            case 1001: // Nublado
                return R.drawable.ic_clima_nublado; // Este era ic_clima_nublado.png
            case 1100: // Mayormente Despejado
                return R.drawable.ic_clima_casi_despejado; // Este era ic_clima_casi_despejado.png
            case 1101: // Parcialmente Nublado
                return R.drawable.ic_clima_parcialmente_nublado; // Este era ic_clima_parcialmente_nublado.png
            case 1102: // Mayormente Nublado
                return R.drawable.ic_clima_mayormente_nublado; // Este era ic_clima_mayormente_nublado.png

            case 2000: // Niebla
            case 2100: // Niebla ligera
                return R.drawable.ic_clima_niebla; // Este era ic_clima_niebla.png

            case 3000: // Viento ligero
            case 3001: // Viento
            case 3002: // Viento fuerte
                return R.drawable.ic_clima_viento; // Este era ic_clima_viento.png

            case 4000: // Llovizna
                return R.drawable.ic_clima_llovizna; // Este era ic_clima_llovizna.png
            case 4001: // Lluvia
            case 4200: // Lluvia ligera
            case 4201: // Lluvia fuerte
                return R.drawable.ic_clima_lluvia; // Este era ic_clima_lluvia.png

            case 5000: // Nieve
            case 5001: // Nieve ligera
            case 5100: // Nieve fuerte
                return R.drawable.ic_clima_nieve; // Este era ic_clima_nieve.png

            case 6000: // Llovizna helada
            case 6001: // Lluvia helada
            case 6200: // Lluvia helada ligera
            case 6201: // Lluvia helada fuerte
                return R.drawable.ic_clima_lluvia_helada; // Este era ic_clima_lluvia_helada.png

            case 8000: // Tormenta
                return R.drawable.ic_clima_tormenta; // Este era ic_clima_tormenta.png

            // Cualquier otro código de clima de Tomorrow.io que NO esté
            // explícitamente listado arriba, por defecto usará el icono de "desconocido".
            default:
                return R.drawable.ic_clima_desconocido; // Este era ic_clima_desconocido.png
        }
    }

    public static String getWeatherConditionText(Integer weatherCode) {
        if (weatherCode == null) {
            return "Desconocido";
        }

        switch (weatherCode) {
            case 1000:
                return "Despejado";
            case 1001:
                return "Nublado";
            case 1100:
                return "Mayormente despejado";
            case 1101:
                return "Parcialmente nublado";
            case 1102:
                return "Mayormente nublado";

            case 2000:
            case 2100:
                return "Niebla";

            case 3000:
            case 3001:
            case 3002:
                return "Viento";

            case 4000:
                return "Llovizna";
            case 4001:
            case 4200:
            case 4201:
                return "Lluvia";

            case 5000:
            case 5001:
            case 5100:
                return "Nieve";

            case 6000:
            case 6001:
            case 6200:
            case 6201:
                return "Lluvia Helada";

            case 8000:
                return "Tormenta";

            default:
                return "Desconocido";
        }
    }
}