// @ts-ignore
import * as CKEditor from '../ckeditor5/ckeditor5.js';
import { showToast } from "./toast.js";
const { ClassicEditor, Autosave, Essentials, Paragraph, Autoformat, TextTransformation, LinkImage, Link, ImageBlock, ImageToolbar, BlockQuote, Bold, ImageUpload, ImageInsertViaUrl, AutoImage, Table, TableToolbar, Emoji, Mention, Heading, ImageTextAlternative, ImageCaption, ImageResize, ImageStyle, Indent, IndentBlock, ImageInline, Italic, List, TableCaption, TodoList, Underline, Fullscreen, Code, Strikethrough, FontBackgroundColor, FontColor, FontFamily, FontSize, Highlight, Alignment, TableColumnResize, TableCellProperties, TableProperties } = CKEditor;
const editorConfig = {
    toolbar: {
        items: [
            'undo',
            'redo',
            '|',
            'fullscreen',
            '|',
            'heading',
            '|',
            'fontSize',
            'fontFamily',
            'fontColor',
            'fontBackgroundColor',
            '|',
            'bold',
            'italic',
            'underline',
            'strikethrough',
            'code',
            '|',
            'emoji',
            'link',
            'uploadImage',
            'insertTable',
            'blockQuote',
            '|',
            'alignment',
            '|',
            'bulletedList',
            'numberedList',
            'todoList',
            'outdent',
            'indent'
        ],
        shouldNotGroupWhenFull: false
    },
    plugins: [
        Alignment,
        Autoformat,
        AutoImage,
        Autosave,
        BlockQuote,
        Bold,
        Code,
        Emoji,
        Essentials,
        FontBackgroundColor,
        FontColor,
        FontFamily,
        FontSize,
        Fullscreen,
        Heading,
        Highlight,
        ImageBlock,
        ImageCaption,
        ImageInline,
        ImageInsertViaUrl,
        ImageResize,
        ImageStyle,
        ImageTextAlternative,
        ImageToolbar,
        ImageUpload,
        Indent,
        IndentBlock,
        Italic,
        Link,
        LinkImage,
        List,
        Mention,
        Paragraph,
        Strikethrough,
        Table,
        TableCaption,
        TableCellProperties,
        TableColumnResize,
        TableProperties,
        TableToolbar,
        TextTransformation,
        TodoList,
        Underline
    ],
    fontFamily: {
        supportAllValues: true
    },
    fontSize: {
        options: [10, 12, 14, 'default', 18, 20, 22],
        supportAllValues: true
    },
    fullscreen: {
        onEnterCallback: (container) => container.classList.add('editor-container', 'editor-container_classic-editor', 'editor-container_include-fullscreen', 'main-container'),
    },
    heading: {
        options: [
            {
                model: 'paragraph',
                title: 'Paragraph',
                class: 'ck-heading_paragraph'
            },
            {
                model: 'heading1',
                view: 'h1',
                title: 'Heading 1',
                class: 'ck-heading_heading1'
            },
            {
                model: 'heading2',
                view: 'h2',
                title: 'Heading 2',
                class: 'ck-heading_heading2'
            },
            {
                model: 'heading3',
                view: 'h3',
                title: 'Heading 3',
                class: 'ck-heading_heading3'
            },
            {
                model: 'heading4',
                view: 'h4',
                title: 'Heading 4',
                class: 'ck-heading_heading4'
            },
        ]
    },
    image: {
        toolbar: [
            'toggleImageCaption',
            'imageTextAlternative',
            '|',
            'imageStyle:inline',
            'imageStyle:wrapText',
            'imageStyle:breakText',
            '|',
            'resizeImage'
        ]
    },
    licenseKey: 'GPL',
    link: {
        addTargetToExternalLinks: true,
        defaultProtocol: 'https://',
        decorators: {
            toggleDownloadable: {
                mode: 'manual',
                label: 'Downloadable',
                attributes: {
                    download: 'file'
                }
            }
        }
    },
    placeholder: '내용을 입력해주세요',
    table: {
        contentToolbar: ['tableColumn', 'tableRow', 'mergeTableCells', 'tableProperties', 'tableCellProperties']
    }
};
class CustomUploadAdapter {
    loader;
    url;
    constructor(loader, url) {
        this.loader = loader;
        this.url = url;
    }
    upload() {
        return this.loader.file.then((file) => new Promise((resolve, reject) => {
            const MAX_FILE_SIZE = 10 * 1024 * 1024;
            if (file.size > MAX_FILE_SIZE) {
                const message = "파일 하나당 10MB 이하만 업로드 가능합니다.";
                showToast(message, "error");
                return reject(new Error(message));
            }
            const formData = new FormData();
            formData.append('upload', file);
            fetch(this.url, {
                method: 'POST',
                body: formData,
            })
                .then(res => res.json())
                .then(result => resolve({ default: result.url }))
                .catch(err => reject(err));
        }));
    }
    abort() {
        console.log('업로드 중단');
    }
}
export class EditorManager {
    instance = null;
    /**
     * 에디터 생성
     * @param selector HTML 태그 아이디 (ex: '#editor')
     * @param uploadUrl 이미지 업로드 API 주소
     */
    async init(selector, uploadUrl) {
        if (this.instance)
            return; // 이미 있으면 중복 생성 방지
        try {
            this.instance = await ClassicEditor.create(document.querySelector(selector), editorConfig);
            this.instance.plugins.get('FileRepository').createUploadAdapter = (loader) => {
                return new CustomUploadAdapter(loader, uploadUrl);
            };
        }
        catch (error) {
            console.error('에디터 초기화 실패:', error);
        }
    }
    // 에디터 내용 가져오기
    getData() {
        return this.instance ? this.instance.getData() : '';
    }
    // 에디터 파괴 (모달 닫을 때 메모리 해제)
    destroy() {
        if (this.instance) {
            this.instance.destroy()
                .then(() => {
                this.instance = null; // 참조를 반드시 비워야 다시 init 가능!
            });
        }
    }
}
