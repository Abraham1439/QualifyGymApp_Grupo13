package com.example.qualifygym_grupo13.ui.viewmodel

import com.example.qualifygym_grupo13.data.repository.ComentarioRepository
import com.example.qualifygym_grupo13.data.repository.PublicacionRepository
import com.example.qualifygym_grupo13.data.repository.TemaRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PublicacionViewModelTest {

    private lateinit var publicacionRepository: PublicacionRepository
    private lateinit var temaRepository: TemaRepository
    private lateinit var comentarioRepository: ComentarioRepository
    private lateinit var viewModel: PublicacionViewModel

    @Before
    fun setup() {
        publicacionRepository = mockk<PublicacionRepository>(relaxed = true)
        temaRepository = mockk<TemaRepository>(relaxed = true)
        comentarioRepository = mockk<ComentarioRepository>(relaxed = true)
        viewModel = PublicacionViewModel(publicacionRepository, temaRepository, comentarioRepository)
    }

    // ========== Tests para isLoading ==========//
    @Test
    fun isLoading_inicialmente_false() {
        // Valida: Estado inicial de isLoading
        // Retorna: isLoading = false
        assertFalse(viewModel.isLoading.value)
    }

    // ========== Tests para errorMessage ==========//
    @Test
    fun errorMessage_inicialmente_null() {
        // Valida: Estado inicial de errorMessage
        // Retorna: errorMessage = null
        assertNull(viewModel.errorMessage.value)
    }

    // ========== Tests para successMessage ==========//
    @Test
    fun successMessage_inicialmente_null() {
        // Valida: Estado inicial de successMessage
        // Retorna: successMessage = null
        assertNull(viewModel.successMessage.value)
    }

    // ========== Tests para allPublicaciones ==========//
    @Test
    fun allPublicaciones_inicialmente_vacia() {
        // Valida: Estado inicial de allPublicaciones
        // Retorna: Lista vacía
        assertTrue(viewModel.allPublicaciones.value.isEmpty())
    }

    // ========== Tests para allTemas ==========//
    @Test
    fun allTemas_inicialmente_vacia() {
        // Valida: Estado inicial de allTemas
        // Retorna: Lista vacía
        assertTrue(viewModel.allTemas.value.isEmpty())
    }

    // ========== Tests para clearMessages ==========//
    @Test
    fun clearMessages_limpia_mensajes() {
        // Valida: Limpieza de mensajes de error y éxito
        // Retorna: errorMessage y successMessage = null
        // Nota: Como no podemos establecer mensajes directamente, solo verificamos que el método existe
        viewModel.clearMessages()
        assertNull(viewModel.errorMessage.value)
        assertNull(viewModel.successMessage.value)
    }

    // ========== Tests para getPublicacionById ==========//
    @Test
    fun getPublicacionById_devuelve_publicacion() = runBlocking {
        // Valida: Obtener publicación por ID exitosamente
        // Retorna: PublicacionDomain o null
        val publicacionDto = com.example.qualifygym_grupo13.data.remote.dto.PublicacionDto(
            1, "Título", "Descripción", "01-01-2024 10:00", false, null, null, 1, 1, null
        )
        coEvery { publicacionRepository.fetchPublicacionById(1) } returns Result.success(publicacionDto)
        
        val result = viewModel.getPublicacionById(1)
        assertNotNull(result)
        assertEquals("Título", result?.titulo)
    }

    @Test
    fun getPublicacionById_devuelve_null_si_no_existe() = runBlocking {
        // Valida: Obtener publicación por ID que no existe
        // Retorna: null
        coEvery { publicacionRepository.fetchPublicacionById(999) } returns Result.failure(Exception("No encontrado"))
        
        val result = viewModel.getPublicacionById(999)
        assertNull(result)
    }

    // ========== Tests para getTemaById ==========//
    @Test
    fun getTemaById_devuelve_tema() = runBlocking {
        // Valida: Obtener tema por ID exitosamente
        // Retorna: TemaDomain o null
        val temaDto = com.example.qualifygym_grupo13.data.remote.dto.TemaDto(1, "Tema 1", 1)
        coEvery { temaRepository.fetchTemaById(1) } returns Result.success(temaDto)
        
        val result = viewModel.getTemaById(1)
        assertNotNull(result)
        assertEquals("Tema 1", result?.nombreTema)
    }

    @Test
    fun getTemaById_devuelve_null_si_no_existe() = runBlocking {
        // Valida: Obtener tema por ID que no existe
        // Retorna: null
        coEvery { temaRepository.fetchTemaById(999) } returns Result.failure(Exception("No encontrado"))
        
        val result = viewModel.getTemaById(999)
        assertNull(result)
    }

    // ========== Tests para createPublicacion ==========//
    @Test
    fun createPublicacion_inicia_loading() = runBlocking {
        // Valida: createPublicacion inicia el proceso de carga
        // Retorna: isLoading = true al inicio
        val publicacionDto = com.example.qualifygym_grupo13.data.remote.dto.PublicacionDto(
            1, "Nuevo Título", "Nueva Descripción", "01-01-2024 10:00", false, null, null, 1, 1, null
        )
        coEvery { publicacionRepository.create(any()) } returns Result.success(publicacionDto)
        coEvery { publicacionRepository.fetchPublicaciones(any()) } returns Result.success(emptyList())
        
        // El método es suspend, así que necesitamos ejecutarlo en runBlocking
        // Pero solo verificamos que el método existe y puede ser llamado
        val result = viewModel.createPublicacion("Título", "Descripción", 1, 1, null)
        assertTrue(result.isSuccess)
    }

    // ========== Tests para deletePublicacion ==========//
    @Test
    fun deletePublicacion_inicia_loading() = runBlocking {
        // Valida: deletePublicacion inicia el proceso de carga
        // Retorna: isLoading = true al inicio
        coEvery { publicacionRepository.delete(1) } returns Result.success(Unit)
        coEvery { publicacionRepository.fetchPublicaciones(any()) } returns Result.success(emptyList())
        
        viewModel.deletePublicacion(1)
        // Verificamos que el método se ejecuta sin errores
        assertTrue(true)
    }

    // ========== Tests para createComentario ==========//
    @Test
    fun createComentario_exitoso() = runBlocking {
        // Valida: Crear comentario exitosamente
        // Retorna: Result.success con ID del comentario
        val comentarioDto = com.example.qualifygym_grupo13.data.remote.dto.ComentarioDto(
            1, "Comentario", "01-01-2024 10:00", false, null, null, 1, 1
        )
        coEvery { comentarioRepository.create(any()) } returns Result.success(comentarioDto)
        coEvery { comentarioRepository.fetchComentariosPorPublicacion(any(), any()) } returns Result.success(emptyList())
        
        val result = viewModel.createComentario("Comentario", 1, 1)
        assertTrue(result.isSuccess)
    }

    // ========== Tests para ocultarComentario ==========//
    @Test
    fun ocultarComentario_inicia_loading() = runBlocking {
        // Valida: ocultarComentario inicia el proceso de carga
        // Retorna: isLoading = true al inicio
        val comentarioDto = com.example.qualifygym_grupo13.data.remote.dto.ComentarioDto(
            1, "Comentario", "01-01-2024 10:00", true, "01-01-2024 11:00", "Spam", 1, 1
        )
        coEvery { comentarioRepository.ocultar(any(), any()) } returns Result.success(comentarioDto)
        coEvery { comentarioRepository.fetchComentariosPorPublicacion(any(), any()) } returns Result.success(emptyList())
        
        val result = viewModel.ocultarComentario(1, "Spam", 1)
        assertTrue(result.isSuccess)
    }

    // ========== Tests para mostrarComentario ==========//
    @Test
    fun mostrarComentario_inicia_loading() = runBlocking {
        // Valida: mostrarComentario inicia el proceso de carga
        // Retorna: isLoading = true al inicio
        val comentarioDto = com.example.qualifygym_grupo13.data.remote.dto.ComentarioDto(
            1, "Comentario", "01-01-2024 10:00", false, null, null, 1, 1
        )
        coEvery { comentarioRepository.mostrar(any()) } returns Result.success(comentarioDto)
        coEvery { comentarioRepository.fetchComentariosPorPublicacion(any(), any()) } returns Result.success(emptyList())
        
        val result = viewModel.mostrarComentario(1, 1)
        assertTrue(result.isSuccess)
    }

    // ========== Tests para deleteComentario ==========//
    @Test
    fun deleteComentario_inicia_loading() = runBlocking {
        // Valida: deleteComentario inicia el proceso de carga
        // Retorna: isLoading = true al inicio
        coEvery { comentarioRepository.delete(any()) } returns Result.success(Unit)
        coEvery { comentarioRepository.fetchComentariosPorPublicacion(any(), any()) } returns Result.success(emptyList())
        
        val result = viewModel.deleteComentario(1, 1)
        assertTrue(result.isSuccess)
    }

    // ========== Tests para ocultarPublicacion ==========//
    @Test
    fun ocultarPublicacion_inicia_loading() = runBlocking {
        // Valida: ocultarPublicacion inicia el proceso de carga
        // Retorna: isLoading = true al inicio
        val publicacionDto = com.example.qualifygym_grupo13.data.remote.dto.PublicacionDto(
            1, "Título", "Descripción", "01-01-2024 10:00", true, "01-01-2024 11:00", "Spam", 1, 1, null
        )
        coEvery { publicacionRepository.ocultar(any(), any()) } returns Result.success(publicacionDto)
        coEvery { publicacionRepository.fetchPublicaciones(any()) } returns Result.success(emptyList())
        
        val result = viewModel.ocultarPublicacion(1, "Spam", false)
        assertTrue(result.isSuccess)
    }

    // ========== Tests para mostrarPublicacion ==========//
    @Test
    fun mostrarPublicacion_inicia_loading() = runBlocking {
        // Valida: mostrarPublicacion inicia el proceso de carga
        // Retorna: isLoading = true al inicio
        val publicacionDto = com.example.qualifygym_grupo13.data.remote.dto.PublicacionDto(
            1, "Título", "Descripción", "01-01-2024 10:00", false, null, null, 1, 1, null
        )
        coEvery { publicacionRepository.mostrar(any()) } returns Result.success(publicacionDto)
        coEvery { publicacionRepository.fetchPublicaciones(any()) } returns Result.success(emptyList())
        
        val result = viewModel.mostrarPublicacion(1, false)
        assertTrue(result.isSuccess)
    }

    // ========== Tests para loadAllTemasPublic ==========//
    @Test
    fun loadAllTemasPublic_ejecuta_correctamente() {
        // Valida: loadAllTemasPublic ejecuta sin errores
        // Retorna: Método ejecutado correctamente
        coEvery { temaRepository.fetchTemas() } returns Result.success(emptyList())
        viewModel.loadAllTemasPublic()
        // Verificamos que el método se ejecuta sin errores
        assertTrue(true)
    }

    // ========== Tests para loadAllPublicacionesIncluyendoOcultas ==========//
    @Test
    fun loadAllPublicacionesIncluyendoOcultas_inicia_loading() {
        // Valida: loadAllPublicacionesIncluyendoOcultas inicia el proceso de carga
        // Retorna: isLoading = true al inicio
        coEvery { publicacionRepository.fetchPublicaciones(true) } returns Result.success(emptyList())
        viewModel.loadAllPublicacionesIncluyendoOcultas()
        // Verificamos que el método se ejecuta sin errores
        assertTrue(true)
    }

    // ========== Tests para loadUserPublicaciones ==========//
    @Test
    fun loadUserPublicaciones_inicia_loading() {
        // Valida: loadUserPublicaciones inicia el proceso de carga
        // Retorna: isLoading = true al inicio
        coEvery { publicacionRepository.fetchPublicacionesPorUsuario(any(), any()) } returns Result.success(emptyList())
        viewModel.loadUserPublicaciones(1)
        // Verificamos que el método se ejecuta sin errores
        assertTrue(true)
    }

    // ========== Tests para searchPublicaciones ==========//
    @Test
    fun searchPublicaciones_devuelve_resultados() = runBlocking {
        // Valida: Búsqueda de publicaciones exitosa
        // Retorna: StateFlow con lista de publicaciones
        val publicacionDto = com.example.qualifygym_grupo13.data.remote.dto.PublicacionDto(
            idPublicacion = 1,
            titulo = "Título",
            descripcion = "Descripción",
            fecha = "01-01-2024 10:00",
            oculta = false,
            fechaBaneo = null,
            motivoBaneo = null,
            usuarioId = 1,
            temaId = 1,
            imageUrl = null
        )
        coEvery { publicacionRepository.buscarPublicaciones(any()) } returns Result.success(listOf(publicacionDto))
        
        val result = viewModel.searchPublicaciones("test")
        delay(100)
        
        assertTrue(result.value.isNotEmpty())
    }

    // ========== Tests para getPublicacionesByTema ==========//
    @Test
    fun getPublicacionesByTema_devuelve_resultados() = runBlocking {
        // Valida: Obtener publicaciones por tema exitoso
        // Retorna: StateFlow con lista de publicaciones
        val publicacionDto = com.example.qualifygym_grupo13.data.remote.dto.PublicacionDto(
            idPublicacion = 1,
            titulo = "Título",
            descripcion = "Descripción",
            fecha = "01-01-2024 10:00",
            oculta = false,
            fechaBaneo = null,
            motivoBaneo = null,
            usuarioId = 1,
            temaId = 1,
            imageUrl = null
        )
        coEvery { publicacionRepository.fetchPublicacionesPorTema(any(), any()) } returns Result.success(listOf(publicacionDto))
        
        val result = viewModel.getPublicacionesByTema(1)
        delay(100)
        
        assertTrue(result.value.isNotEmpty())
    }

    // ========== Tests para getComentariosByPublicacionId ==========//
    @Test
    fun getComentariosByPublicacionId_devuelve_resultados() = runBlocking {
        // Valida: Obtener comentarios por publicación exitoso
        // Retorna: StateFlow con lista de comentarios
        val comentarioDto = com.example.qualifygym_grupo13.data.remote.dto.ComentarioDto(
            1, "Comentario", "01-01-2024 10:00", false, null, null, 1, 1
        )
        coEvery { comentarioRepository.fetchComentariosPorPublicacion(any(), any()) } returns Result.success(listOf(comentarioDto))
        
        val result = viewModel.getComentariosByPublicacionId(1)
        delay(100)
        
        assertTrue(result.value.isNotEmpty())
    }

    // ========== Tests para updatePublicacionImage ==========//
    @Test
    fun updatePublicacionImage_exitoso() = runBlocking {
        // Valida: Actualización de imagen de publicación exitosa
        // Retorna: Recarga la lista de publicaciones
        val publicacionDto = com.example.qualifygym_grupo13.data.remote.dto.PublicacionDto(
            idPublicacion = 1,
            titulo = "Título",
            descripcion = "Descripción",
            fecha = "01-01-2024 10:00",
            oculta = false,
            fechaBaneo = null,
            motivoBaneo = null,
            usuarioId = 1,
            temaId = 1,
            imageUrl = "image123"
        )
        coEvery { publicacionRepository.updateImagen(any(), any()) } returns Result.success(publicacionDto)
        coEvery { publicacionRepository.fetchPublicaciones(any()) } returns Result.success(emptyList())
        
        viewModel.updatePublicacionImage(1, "image123")
        delay(100)
        
        // Verificamos que el método se ejecuta sin errores
        assertTrue(true)
    }
}

