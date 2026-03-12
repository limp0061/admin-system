/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './src/main/resources/templates/**/*.html',
    './src/main/resources/static/**/*.js',
  ],
  safelist: [
    'toast-success',
    'toast-error',
    'dark:bg-[#1e1e1e]',
    'dark:bg-[#2a2a2a]',
    'dark:hover:bg-[#333333]',
    'dark:text-gray-600',
    'dark:text-blue-400',
    'dark:text-gray-100',
    'dark:border-white',
  ],
  darkMode: 'class',
  theme: {
    extend: {},
  },
  plugins: [require('@tailwindcss/typography')],
};
