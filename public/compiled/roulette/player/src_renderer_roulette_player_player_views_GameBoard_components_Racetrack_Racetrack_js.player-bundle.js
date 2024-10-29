"use strict";
/*
 * ATTENTION: The "eval" devtool has been used (maybe by default in mode: "development").
 * This devtool is neither made for production nor for readable output files.
 * It uses "eval()" calls to create a separate source file in the browser devtools.
 * If you are trying to read the output file, select a different devtool (https://webpack.js.org/configuration/devtool/)
 * or disable the default devtool with "devtool: false".
 * If you are looking for production-ready output files, see mode: "production" (https://webpack.js.org/configuration/mode/).
 */
(self["webpackChunktykhe_electron_apps_roulette"] = self["webpackChunktykhe_electron_apps_roulette"] || []).push([["src_renderer_roulette_player_player_views_GameBoard_components_Racetrack_Racetrack_js"],{

/***/ "./src/renderer/roulette/player/player/views/GameBoard/components/Racetrack/Racetrack.js":
/*!***********************************************************************************************!*\
  !*** ./src/renderer/roulette/player/player/views/GameBoard/components/Racetrack/Racetrack.js ***!
  \***********************************************************************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

eval("__webpack_require__.r(__webpack_exports__);\n/* harmony export */ __webpack_require__.d(__webpack_exports__, {\n/* harmony export */   \"default\": () => (__WEBPACK_DEFAULT_EXPORT__),\n/* harmony export */   isGroupBet: () => (/* binding */ isGroupBet),\n/* harmony export */   isNeighborBet: () => (/* binding */ isNeighborBet)\n/* harmony export */ });\n/* harmony import */ var react_redux__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! react-redux */ \"./node_modules/react-redux/es/index.js\");\n/* harmony import */ var ramda__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ramda */ \"./node_modules/ramda/es/applySpec.js\");\n/* harmony import */ var _components__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../../../../components */ \"./src/renderer/roulette/player/player/components/index.js\");\n/* harmony import */ var _store_actions_bets__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../../../../store/actions/bets */ \"./src/renderer/roulette/player/player/store/actions/bets.js\");\n/* harmony import */ var _store_selectors__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../../../../store/selectors */ \"./src/renderer/roulette/player/player/store/selectors/index.js\");\n\n\n\n\n\nconst mapStateToProps = (0,ramda__WEBPACK_IMPORTED_MODULE_4__[\"default\"])({\n  bets: _store_selectors__WEBPACK_IMPORTED_MODULE_3__.currentBetsSelector,\n  frenchBets: _store_selectors__WEBPACK_IMPORTED_MODULE_3__.frenchBetsSelector,\n  chips: _store_selectors__WEBPACK_IMPORTED_MODULE_3__.chipsSelector,\n  selectedChip: _store_selectors__WEBPACK_IMPORTED_MODULE_3__.selectedChipSelector,\n  idle: _store_selectors__WEBPACK_IMPORTED_MODULE_3__.idleGameBoardSelector,\n  format: _store_selectors__WEBPACK_IMPORTED_MODULE_3__.formatSelector,\n  american: _store_selectors__WEBPACK_IMPORTED_MODULE_3__.isAmericanRouletteSelector\n});\n\n// Should be moved in @ezugi/utils\nconst isNeighborBet = index => index > 10000;\nconst isGroupBet = index => index >= 1000 && !isNeighborBet(index);\nconst {\n  neighborBet,\n  frenchBet\n} = _store_actions_bets__WEBPACK_IMPORTED_MODULE_2__[\"default\"];\nconst mapDispatchToProps = {\n  onClick: (index, value) => {\n    if (isGroupBet(index)) {\n      return frenchBet.add({\n        [index]: {\n          index,\n          value\n        }\n      });\n    }\n    return neighborBet.add({\n      [index]: {\n        index,\n        value\n      }\n    });\n  }\n};\n/* harmony default export */ const __WEBPACK_DEFAULT_EXPORT__ = ((0,react_redux__WEBPACK_IMPORTED_MODULE_0__.connect)(mapStateToProps, mapDispatchToProps)(_components__WEBPACK_IMPORTED_MODULE_1__.Racetrack));\n\n//# sourceURL=webpack://tykhe-electron-apps-roulette/./src/renderer/roulette/player/player/views/GameBoard/components/Racetrack/Racetrack.js?");

/***/ })

}]);