export default {
  content: ['./index.html', './src/**/*.{js,jsx,ts,tsx}'],
  theme: {
    extend: {
      colors: {
        brand: {
          50:'#eef2ff',100:'#e0e7ff',200:'#c7d2fe',300:'#a5b4fc',400:'#818cf8',
          500:'#6366f1',  
          600:'#5458e3',  
          700:'#4f46e5',800:'#4338ca',900:'#3730a3'
        },
      },
      boxShadow: {
        glass: '0 8px 32px rgba(31,38,135,.25)', 
      },
      fontFamily: {
        sans: ['Poppins','system-ui','Segoe UI','Roboto','Arial','sans-serif'],
      },
    },
  },
  plugins: [],
};
