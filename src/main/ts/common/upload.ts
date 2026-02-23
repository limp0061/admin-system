export const previewImage = (input: HTMLInputElement): void => {
    if (input.files && input.files[0]) {
        const reader = new FileReader();

        reader.onload = (e: ProgressEvent<FileReader>) => {
            const preview = document.getElementById('image-preview');
            const placeholder = document.getElementById('placeholder');
            const previewImg = document.getElementById('preview-img') as HTMLImageElement;

            if (preview && previewImg && e.target?.result) {

                preview.classList.add('border-solid', 'border-gray-400');
                preview.classList.remove('border-dashed');

                placeholder?.classList.add('hidden');

                previewImg.classList.remove('hidden')
                previewImg.src = e.target.result as string;
            }
        };

        reader.readAsDataURL(input.files[0]);
    }
}