/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{html,vue,js,ts}"],
  theme: {
    colors: {
      inherit: "inherit",
      current: "currentColor",
      transparent: "transparent",
      black: "#0b0c0c",
      white: "#fff",
      blue: {
        200: "#ecf1f4",
        300: "#dce8ef",
        500: "#b3c9d6",
        600: "#6693ad",
        700: "#336f91",
        800: "#004b76",
        900: "#003350",
      },
      gray: {
        100: "#f6f7f8",
        400: "#dcdee1",
        600: "#b8bdc3",
        800: "#717a88",
        900: "#4e596a",
      },
      green: {
        700: "#01854a",
      },
      orange: {
        400: "#f8c967",
      },
      red: {
        200: "#f9e5ec",
        800: "#b0243f",
      },
      yellow: {
        300: "#f9ec9e",
        400: "#f7e67d",
        500: "#f5e05d",
        700: "#e5ce5c",
        900: "#c3a91e",
      },
      neutral: {
        20: "#f4f5f7",
        700: "#253858",
      },
      background: "rgba(184, 189, 195, 0.5)",
    },
    fontFamily: {
      regular: [
        "BundesSansWeb",
        "Calibri",
        "Verdana",
        "Arial",
        "Helvetica",
        "sans-serif",
      ],
      bold: [
        "BundesSansWeb Bold",
        "Calibri",
        "Verdana",
        "Arial",
        "Helvetica",
        "sans-serif",
      ],
    },
    fontWeight: {
      normal: 400,
      bold: 700,
    },
    fontSize: {
      base: "1rem",
      10: "0.625rem",
      11: "0.688rem",
      14: "0.875rem",
      16: "1rem",
      18: "1.125rem",
      20: "1.25rem",
      22: "1.375rem",
      24: "1.5rem",
      28: "1.75rem",
      30: "1.875rem",
      32: "2rem",
      48: "3rem",
      56: "3.5rem",
      64: "4rem",
      72: "4.5rem",
    },
    lineHeight: {
      default: "1.625 !important",
      13: "0.8125rem",
      16: "1rem",
      18: "1.125rem",
      20: "1.25rem",
      22: "1.375rem",
      24: "1.5rem",
      26: "1.625rem",
      30: "1.875rem",
      36: "2.25rem",
      40: "2.5rem",
      68: "4.25rem",
    },
    spacing: {
      0: "0",
      1: "0.0625rem",
      2: "0.125rem",
      4: "0.25rem",
      6: "0.375rem",
      8: "0.5rem",
      10: "0.625rem",
      12: "0.75rem",
      14: "0.875rem",
      16: "1rem",
      20: "1.25rem",
      24: "1.5rem",
      28: "1.75rem",
      32: "2rem",
      36: "2.25rem",
      40: "2.5rem",
      44: "2.75rem",
      48: "3rem",
      56: "3.5rem",
      64: "4rem",
      80: "5rem",
      96: "6rem",
      112: "7rem",
      128: "8rem",
      144: "9rem",
      160: "10rem",
      176: "11rem",
      192: "12rem",
      208: "13rem",
      224: "14rem",
      240: "15rem",
      288: "18rem",
      320: "20rem",
      384: "24rem",
    },
    extend: {
      borderWidth: {
        1: "1px",
        3: "3px",
      },
      boxShadow: {
        error: "inset 0 0 0 50px bg-red-200",
        white: "inset 0 0 0 50px bg-white",
        focus: "inset 0 0 0 2px bg-blue-900",
        hover: "inset 0 0 0 2px bg-blue-900",
      },
    },
  },
  plugins: [],
}
