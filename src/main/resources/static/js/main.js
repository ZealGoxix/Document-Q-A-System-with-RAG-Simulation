document.addEventListener('DOMContentLoaded', function() {
    // DOM Elements
    const questionForm = document.getElementById('questionForm');
    const questionInput = document.getElementById('questionInput');
    const answerContainer = document.getElementById('answerContainer');
    const loadingContainer = document.getElementById('loadingContainer');
    const sourcesContainer = document.getElementById('sourcesContainer');
    const sourcesList = document.getElementById('sourcesList');
    const confidenceScore = document.getElementById('confidenceScore');
    const processingTime = document.getElementById('processingTime');
    const clearBtn = document.getElementById('clearBtn');
    
    // Sample question buttons
    const sampleButtons = document.querySelectorAll('.sample-btn');
    
    // Question form submission
    if (questionForm) {
        questionForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const question = questionInput.value.trim();
            if (!question) {
                showError('Please enter a question');
                return;
            }
            
            // Show loading, hide answer
            loadingContainer.classList.remove('hidden');
            answerContainer.classList.add('hidden');
            sourcesContainer.classList.add('hidden');
            
            try {
                const response = await fetch('/ask', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: new URLSearchParams({
                        'question': question
                    })
                });
                
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                
                const data = await response.json();
                
                // Hide loading, show answer
                loadingContainer.classList.add('hidden');
                answerContainer.classList.remove('hidden');
                
                // Display answer
                answerContainer.innerHTML = `
                    <div class="answer">
                        <div class="question-display">
                            <strong>Question:</strong> ${escapeHtml(question)}
                        </div>
                        <div class="answer-text">${formatAnswer(data.answer)}</div>
                    </div>
                `;
                
                // Display sources if available
                if (data.sourceChunks && data.sourceChunks.length > 0) {
                    sourcesList.innerHTML = '';
                    data.sourceChunks.forEach((chunk, index) => {
                        const sourceDiv = document.createElement('div');
                        sourceDiv.className = 'source-chunk';
                        sourceDiv.innerHTML = `
                            <strong>Source ${index + 1}:</strong>
                            <p>${escapeHtml(chunk.substring(0, 200))}${chunk.length > 200 ? '...' : ''}</p>
                        `;
                        sourcesList.appendChild(sourceDiv);
                    });
                    
                    confidenceScore.textContent = Math.round(data.confidence * 100);
                    processingTime.textContent = data.processingTimeMs;
                    sourcesContainer.classList.remove('hidden');
                } else {
                    sourcesContainer.classList.add('hidden');
                }
                
            } catch (error) {
                console.error('Error:', error);
                loadingContainer.classList.add('hidden');
                showError(`Error: ${error.message}. Please try again or upload documents first.`);
            }
        });
    }
    
    // Clear button
    if (clearBtn) {
        clearBtn.addEventListener('click', function() {
            questionInput.value = '';
            questionInput.focus();
        });
    }
    
    // Sample question buttons
    sampleButtons.forEach(button => {
        button.addEventListener('click', function() {
            const question = this.getAttribute('data-question');
            questionInput.value = question;
            questionForm.dispatchEvent(new Event('submit'));
        });
    }
    );
    
    // File upload feedback
    const fileInput = document.querySelector('input[type="file"]');
    if (fileInput) {
        fileInput.addEventListener('change', function() {
            if (this.files.length > 0) {
                const fileName = this.files[0].name;
                const label = this.previousElementSibling;
                if (label && label.tagName === 'LABEL') {
                    const small = label.querySelector('small');
                    if (small) {
                        small.textContent = `Selected: ${fileName}`;
                    }
                }
            }
        });
    }
    
    // Helper functions
    function formatAnswer(text) {
        return escapeHtml(text).replace(/\n/g, '<br>');
    }
    
    function escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
    
    function showError(message) {
        answerContainer.innerHTML = `
            <div class="error-message">
                <div style="color: #e53e3e; font-size: 48px; margin-bottom: 10px;">⚠️</div>
                <p style="color: #c53030; font-weight: 500;">${message}</p>
            </div>
        `;
        answerContainer.classList.remove('hidden');
        sourcesContainer.classList.add('hidden');
    }
    
    // Check if documents exist and show appropriate message
    function updatePlaceholder() {
        const documentItems = document.querySelectorAll('.document-item');
        if (documentItems.length === 0) {
            const placeholder = document.querySelector('.placeholder');
            if (placeholder) {
                placeholder.innerHTML = `
                    <p>📁 No documents uploaded yet.</p>
                    <p>Upload a document using the left panel to get started.</p>
                    <p>Supported formats: PDF, TXT</p>
                `;
            }
        }
    }
    
    // Initial update
    updatePlaceholder();
});