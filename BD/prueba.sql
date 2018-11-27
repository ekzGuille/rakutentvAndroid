-- Vista que recopila la cantidad de veces que una pelicula se ha votado así como la media de los votos de dicha pelicula
-- Parámetros de la vista:
--		- idPelicula
-- 		- Veces que se ha votado dicha pelicula 
-- 		- Media de las veces que se ha votado

-- SELECT pelicula.tituloPeli, usuario.email, infopuntuacion.descInfoPuntuacion from pelicula, usuario, infopuntuacion, puntuacion where pelicula.idPelicula = puntuacion.idPelicula and usuario.idUsuario = puntuacion.idUsuario and infopuntuacion.idInfoPuntuacion = puntuacion.idInfoPuntuacion

-- SELECT idPelicula, count(idPelicula) from puntuacion GROUP by idPelicula
-- select idPelicula, sum(idInfoPuntuacion) from puntuacion GROUP by idPelicula
-- select idPelicula,count(idPelicula) 'CUENTA' , sum(idInfoPuntuacion) 'SUMA' , AVG(idInfoPuntuacion) 'MEDIA' from puntuacion GROUP by idPelicula

CREATE VIEW `valoracionglobalpelicula`  AS  
SELECT idPelicula ,COUNT(idPelicula) 'valoracionesTotales', AVG(idInfoPuntuacion ) 'mediaValoraciones' FROM puntuacion GROUP BY idPelicula;


SELECT pelicula.`idPelicula`,`tituloPeli`,`resumenPeli`,`trailerPeli`,`caratulaPeli`,`imagenPeli`,`fechaEstreno`,`audiosDisponibles`,`subtitulosDisponibles`,`duracionPeli`,`precioPeli`, `valoracionesTotales`, `mediaValoraciones` FROM `pelicula` LEFT OUTER JOIN `valoracionglobalpelicula` ON pelicula.idPelicula = valoracionglobalpelicula.idPelicula 


select usuario.idUsuario, pelicula.idPelicula, marcarfavorito.idMarcarFavorito, puntuacion.idPuntuacion, compra.idCompra
from usuario, pelicula left OUTER join marcarfavorito on pelicula.idPelicula = marcarfavorito.idPelicula LEFT OUTER JOIn puntuacion on pelicula.idPelicula = puntuacion.idPelicula left outer JOIN compra on pelicula.idPelicula
where usuario.idUsuario = 3 and pelicula.idPelicula= 5



SELECT usuario.idUsuario, pelicula.idPelicula, marcarfavorito.idMarcarFavorito, puntuacion.idPuntuacion, compra.idCompra
FROM usuario, pelicula LEFT OUTER JOIN marcarfavorito ON pelicula.idPelicula = marcarfavorito.idPelicula LEFT OUTER JOIN puntuacion ON pelicula.idPelicula = puntuacion.idPelicula LEFT OUTER JOIN compra ON pelicula.idPelicula
WHERE usuario.idUsuario = 3 AND pelicula.idPelicula = 5



SELECT usuario.idUsuario, pelicula.idPelicula, marcarfavorito.idMarcarFavorito, puntuacion.idPuntuacion, compra.idCompra FROM usuario, pelicula LEFT OUTER JOIN marcarfavorito ON pelicula.idPelicula = marcarfavorito.idPelicula LEFT OUTER JOIN puntuacion ON pelicula.idPelicula = puntuacion.idPelicula LEFT OUTER JOIN compra ON pelicula.idPelicula = compra.idPelicula WHERE usuario.idUsuario = 3 AND pelicula.idPelicula = 6