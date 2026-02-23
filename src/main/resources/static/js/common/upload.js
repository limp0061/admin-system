export const previewImage = (input) => {
    if (input.files && input.files[0]) {
        const reader = new FileReader();
        reader.onload = (e) => {
            const preview = document.getElementById('image-preview');
            const placeholder = document.getElementById('placeholder');
            const previewImg = document.getElementById('preview-img');
            if (preview && previewImg && e.target?.result) {
                preview.classList.add('border-solid', 'border-gray-400');
                preview.classList.remove('border-dashed');
                placeholder?.classList.add('hidden');
                previewImg.classList.remove('hidden');
                previewImg.src = e.target.result;
            }
        };
        reader.readAsDataURL(input.files[0]);
    }
};
