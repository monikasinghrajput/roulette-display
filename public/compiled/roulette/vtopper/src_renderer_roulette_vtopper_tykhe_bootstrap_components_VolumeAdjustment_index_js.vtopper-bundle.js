"use strict";
/*
 * ATTENTION: The "eval" devtool has been used (maybe by default in mode: "development").
 * This devtool is neither made for production nor for readable output files.
 * It uses "eval()" calls to create a separate source file in the browser devtools.
 * If you are trying to read the output file, select a different devtool (https://webpack.js.org/configuration/devtool/)
 * or disable the default devtool with "devtool: false".
 * If you are looking for production-ready output files, see mode: "production" (https://webpack.js.org/configuration/mode/).
 */
(self["webpackChunktykhe_electron_apps_roulette"] = self["webpackChunktykhe_electron_apps_roulette"] || []).push([["src_renderer_roulette_vtopper_tykhe_bootstrap_components_VolumeAdjustment_index_js"],{

/***/ "./src/renderer/roulette/vtopper/tykhe/bootstrap/components/VolumeAdjustment/index.js":
/*!********************************************************************************************!*\
  !*** ./src/renderer/roulette/vtopper/tykhe/bootstrap/components/VolumeAdjustment/index.js ***!
  \********************************************************************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

eval("__webpack_require__.r(__webpack_exports__);\n/* harmony export */ __webpack_require__.d(__webpack_exports__, {\n/* harmony export */   \"default\": () => (__WEBPACK_DEFAULT_EXPORT__)\n/* harmony export */ });\n/* harmony import */ var react__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! react */ \"./node_modules/react/index.js\");\n/* harmony import */ var react__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(react__WEBPACK_IMPORTED_MODULE_0__);\n/* harmony import */ var lodash_debounce__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! lodash.debounce */ \"./node_modules/lodash.debounce/index.js\");\n/* harmony import */ var lodash_debounce__WEBPACK_IMPORTED_MODULE_1___default = /*#__PURE__*/__webpack_require__.n(lodash_debounce__WEBPACK_IMPORTED_MODULE_1__);\n/* harmony import */ var react_redux__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! react-redux */ \"./node_modules/react-redux/es/index.js\");\n/* harmony import */ var _primitives__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../../../primitives */ \"./src/renderer/roulette/vtopper/tykhe/primitives/index.js\");\n/* harmony import */ var ramda__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ramda */ \"./node_modules/ramda/es/applySpec.js\");\n/* harmony import */ var prop_types__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! prop-types */ \"./node_modules/prop-types/index.js\");\n/* harmony import */ var prop_types__WEBPACK_IMPORTED_MODULE_6___default = /*#__PURE__*/__webpack_require__.n(prop_types__WEBPACK_IMPORTED_MODULE_6__);\n/* harmony import */ var _store_actions_media__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../../store/actions/media */ \"./src/renderer/roulette/vtopper/tykhe/bootstrap/store/actions/media.js\");\n/* harmony import */ var _store_selectors__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../../store/selectors */ \"./src/renderer/roulette/vtopper/tykhe/bootstrap/store/selectors/index.js\");\nfunction _defineProperty(obj, key, value) {\n  if (key in obj) {\n    Object.defineProperty(obj, key, {\n      value: value,\n      enumerable: true,\n      configurable: true,\n      writable: true\n    });\n  } else {\n    obj[key] = value;\n  }\n  return obj;\n}\n\n\n\n\n\n\n\n\nclass VolumeAdjustment extends react__WEBPACK_IMPORTED_MODULE_0__.Component {\n  constructor(props) {\n    super(props);\n    _defineProperty(this, \"updateVolume\", lodash_debounce__WEBPACK_IMPORTED_MODULE_1___default()(() => {\n      const {\n        value\n      } = this.state;\n      const {\n        volume,\n        setVolume\n      } = this.props;\n      if (value !== volume.value) {\n        setVolume({\n          value,\n          muted: parseInt(value, 10) === 0\n        });\n      }\n    }, 100, {\n      leading: true\n    }));\n    _defineProperty(this, \"onChange\", e => {\n      this.setState({\n        value: e.target.value\n      }, this.updateVolume);\n    });\n    this.state = {\n      value: props.volume.value\n    };\n  }\n  render() {\n    const {\n      muted\n    } = this.props.volume;\n    return /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement(_primitives__WEBPACK_IMPORTED_MODULE_3__.Slider, {\n      value: muted ? 0 : this.state.value,\n      min: 0,\n      max: 100,\n      onChange: this.onChange\n    });\n  }\n}\nVolumeAdjustment.propTypes = {\n  setVolume: prop_types__WEBPACK_IMPORTED_MODULE_6__.func,\n  volume: prop_types__WEBPACK_IMPORTED_MODULE_6__.object\n};\nconst {\n  volume\n} = _store_actions_media__WEBPACK_IMPORTED_MODULE_4__[\"default\"];\nconst mapStateToProps = (0,ramda__WEBPACK_IMPORTED_MODULE_7__[\"default\"])({\n  volume: _store_selectors__WEBPACK_IMPORTED_MODULE_5__.volumeSelector\n});\nconst mapDispatchToProps = {\n  setVolume: volume.set\n};\n/* harmony default export */ const __WEBPACK_DEFAULT_EXPORT__ = ((0,react_redux__WEBPACK_IMPORTED_MODULE_2__.connect)(mapStateToProps, mapDispatchToProps)(VolumeAdjustment));\n\n//# sourceURL=webpack://tykhe-electron-apps-roulette/./src/renderer/roulette/vtopper/tykhe/bootstrap/components/VolumeAdjustment/index.js?");

/***/ })

}]);