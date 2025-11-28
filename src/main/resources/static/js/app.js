document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('generateForm');
    const templateInput = document.getElementById('template');
    const jsonDataTextarea = document.getElementById('jsonData');
    const selectedFileName = document.getElementById('selectedFileName');
    const statusMessage = document.getElementById('statusMessage');
    const loadingSpinner = document.getElementById('loadingSpinner');
    const generateBtn = document.getElementById('generateBtn');
    const formatJsonBtn = document.getElementById('formatJson');
    const validateJsonBtn = document.getElementById('validateJson');

    // File selection display
    templateInput.addEventListener('change', function() {
        if (this.files.length > 0) {
            selectedFileName.textContent = '✅ ' + this.files[0].name;
        } else {
            selectedFileName.textContent = '';
        }
    });

    // Format JSON button
    formatJsonBtn.addEventListener('click', function() {
        try {
            const json = JSON.parse(jsonDataTextarea.value);
            jsonDataTextarea.value = JSON.stringify(json, null, 2);
            showStatus('JSON formateado correctamente', 'success');
        } catch (e) {
            showStatus('Error al formatear: ' + e.message, 'error');
        }
    });

    // Validate JSON button
    validateJsonBtn.addEventListener('click', function() {
        try {
            JSON.parse(jsonDataTextarea.value);
            showStatus('✅ JSON válido', 'success');
        } catch (e) {
            showStatus('❌ JSON inválido: ' + e.message, 'error');
        }
    });

    // Sample template buttons
    document.querySelectorAll('.template-sample').forEach(function(btn) {
        btn.addEventListener('click', function() {
            const filename = this.dataset.filename;
            downloadAndSetTemplate(filename);
        });
    });

    // Sample data buttons
    document.querySelectorAll('.data-sample').forEach(function(btn) {
        btn.addEventListener('click', function() {
            const content = this.dataset.content;
            jsonDataTextarea.value = content;
            try {
                const json = JSON.parse(content);
                jsonDataTextarea.value = JSON.stringify(json, null, 2);
            } catch (e) {
                // Keep original if formatting fails
            }
            showStatus('Datos de ejemplo cargados', 'info');
        });
    });

    // Form submission
    form.addEventListener('submit', function(e) {
        e.preventDefault();
        
        // Validate JSON before submitting
        try {
            JSON.parse(jsonDataTextarea.value);
        } catch (e) {
            showStatus('❌ Por favor, corrige el JSON antes de generar: ' + e.message, 'error');
            return;
        }

        if (!templateInput.files.length) {
            showStatus('❌ Por favor, selecciona una plantilla ODT', 'error');
            return;
        }

        generateDocument();
    });

    function generateDocument() {
        const formData = new FormData();
        formData.append('template', templateInput.files[0]);
        formData.append('data', jsonDataTextarea.value);

        showLoading(true);
        hideStatus();

        fetch('/api/generate', {
            method: 'POST',
            body: formData
        })
        .then(function(response) {
            if (!response.ok) {
                return response.json().then(function(error) {
                    throw new Error(error.error || 'Error al generar el documento');
                });
            }
            return response.blob();
        })
        .then(function(blob) {
            // Create download link
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = getOutputFilename();
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            a.remove();
            
            showStatus('✅ ¡Documento generado con éxito! La descarga debería comenzar automáticamente.', 'success');
        })
        .catch(function(error) {
            showStatus('❌ Error: ' + error.message, 'error');
        })
        .finally(function() {
            showLoading(false);
        });
    }

    function downloadAndSetTemplate(filename) {
        showStatus('Descargando plantilla de ejemplo...', 'info');
        
        fetch('/api/samples/templates/' + filename)
            .then(function(response) {
                if (!response.ok) {
                    throw new Error('No se pudo descargar la plantilla');
                }
                return response.blob();
            })
            .then(function(blob) {
                // Create a File object from the blob
                const file = new File([blob], filename, { type: 'application/vnd.oasis.opendocument.text' });
                
                // Create a DataTransfer to set the file input
                const dataTransfer = new DataTransfer();
                dataTransfer.items.add(file);
                templateInput.files = dataTransfer.files;
                
                selectedFileName.textContent = '✅ ' + filename;
                showStatus('Plantilla de ejemplo cargada: ' + filename, 'success');
            })
            .catch(function(error) {
                showStatus('❌ Error: ' + error.message, 'error');
            });
    }

    function getOutputFilename() {
        if (templateInput.files.length > 0) {
            const name = templateInput.files[0].name;
            const baseName = name.replace(/\.odt$/i, '');
            return baseName + '-generated.odt';
        }
        return 'generated-document.odt';
    }

    function showStatus(message, type) {
        statusMessage.textContent = message;
        statusMessage.className = 'status-message ' + type;
        statusMessage.classList.remove('hidden');
    }

    function hideStatus() {
        statusMessage.classList.add('hidden');
    }

    function showLoading(show) {
        if (show) {
            loadingSpinner.classList.remove('hidden');
            generateBtn.disabled = true;
        } else {
            loadingSpinner.classList.add('hidden');
            generateBtn.disabled = false;
        }
    }
});
