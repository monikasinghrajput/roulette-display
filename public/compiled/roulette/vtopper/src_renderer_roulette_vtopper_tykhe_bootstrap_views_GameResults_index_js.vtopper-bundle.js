"use strict";
/*
 * ATTENTION: The "eval" devtool has been used (maybe by default in mode: "development").
 * This devtool is neither made for production nor for readable output files.
 * It uses "eval()" calls to create a separate source file in the browser devtools.
 * If you are trying to read the output file, select a different devtool (https://webpack.js.org/configuration/devtool/)
 * or disable the default devtool with "devtool: false".
 * If you are looking for production-ready output files, see mode: "production" (https://webpack.js.org/configuration/mode/).
 */
(self["webpackChunktykhe_electron_apps_roulette"] = self["webpackChunktykhe_electron_apps_roulette"] || []).push([["src_renderer_roulette_vtopper_tykhe_bootstrap_views_GameResults_index_js"],{

/***/ "./src/renderer/roulette/vtopper/tykhe/bootstrap/views/GameResults/index.js":
/*!**********************************************************************************!*\
  !*** ./src/renderer/roulette/vtopper/tykhe/bootstrap/views/GameResults/index.js ***!
  \**********************************************************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

eval("__webpack_require__.r(__webpack_exports__);\n/* harmony export */ __webpack_require__.d(__webpack_exports__, {\n/* harmony export */   \"default\": () => (__WEBPACK_DEFAULT_EXPORT__)\n/* harmony export */ });\n/* harmony import */ var react__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! react */ \"./node_modules/react/index.js\");\n/* harmony import */ var react__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(react__WEBPACK_IMPORTED_MODULE_0__);\n/* harmony import */ var prop_types__WEBPACK_IMPORTED_MODULE_17__ = __webpack_require__(/*! prop-types */ \"./node_modules/prop-types/index.js\");\n/* harmony import */ var prop_types__WEBPACK_IMPORTED_MODULE_17___default = /*#__PURE__*/__webpack_require__.n(prop_types__WEBPACK_IMPORTED_MODULE_17__);\n/* harmony import */ var _emotion_css__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @emotion/css */ \"./node_modules/@emotion/css/dist/emotion-css.esm.js\");\n/* harmony import */ var react_redux__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! react-redux */ \"./node_modules/react-redux/es/index.js\");\n/* harmony import */ var ramda__WEBPACK_IMPORTED_MODULE_16__ = __webpack_require__(/*! ramda */ \"./node_modules/ramda/es/applySpec.js\");\n/* harmony import */ var bowser__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! bowser */ \"./node_modules/bowser/es5.js\");\n/* harmony import */ var bowser__WEBPACK_IMPORTED_MODULE_3___default = /*#__PURE__*/__webpack_require__.n(bowser__WEBPACK_IMPORTED_MODULE_3__);\n/* harmony import */ var _constants_es__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../../../constants/es */ \"./src/renderer/roulette/vtopper/tykhe/constants/es/index.js\");\n/* harmony import */ var _components__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../../../components */ \"./src/renderer/roulette/vtopper/tykhe/components/index.js\");\n/* harmony import */ var _hooks__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ../../..//hooks */ \"./src/renderer/roulette/vtopper/tykhe/hooks/index.js\");\n/* harmony import */ var _hooks__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ../../hooks */ \"./src/renderer/roulette/vtopper/tykhe/bootstrap/hooks/index.js\");\n/* harmony import */ var _imports__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ../../imports */ \"./src/renderer/roulette/vtopper/tykhe/bootstrap/imports.js\");\n/* harmony import */ var _config__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ../../config */ \"./src/renderer/roulette/vtopper/tykhe/bootstrap/config.js\");\n/* harmony import */ var _store_selectors__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ../../store/selectors */ \"./src/renderer/roulette/vtopper/tykhe/bootstrap/store/selectors/index.js\");\n/* harmony import */ var _store_selectors_jackpot__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! ../../store/selectors/jackpot */ \"./src/renderer/roulette/vtopper/tykhe/bootstrap/store/selectors/jackpot.js\");\n/* harmony import */ var _store_actions_tips__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! ../../store/actions/tips */ \"./src/renderer/roulette/vtopper/tykhe/bootstrap/store/actions/tips.js\");\n/* harmony import */ var _store_actions_menu__WEBPACK_IMPORTED_MODULE_13__ = __webpack_require__(/*! ../../store/actions/menu */ \"./src/renderer/roulette/vtopper/tykhe/bootstrap/store/actions/menu.js\");\n/* harmony import */ var _contexts__WEBPACK_IMPORTED_MODULE_14__ = __webpack_require__(/*! ../../contexts */ \"./src/renderer/roulette/vtopper/tykhe/bootstrap/contexts/index.js\");\n/* harmony import */ var _style__WEBPACK_IMPORTED_MODULE_15__ = __webpack_require__(/*! ./style */ \"./src/renderer/roulette/vtopper/tykhe/bootstrap/views/GameResults/style.js\");\nfunction _extends() {\n  _extends = Object.assign || function (target) {\n    for (var i = 1; i < arguments.length; i++) {\n      var source = arguments[i];\n      for (var key in source) {\n        if (Object.prototype.hasOwnProperty.call(source, key)) {\n          target[key] = source[key];\n        }\n      }\n    }\n    return target;\n  };\n  return _extends.apply(this, arguments);\n}\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nconst ResultMessage = /*#__PURE__*/(0,react__WEBPACK_IMPORTED_MODULE_0__.lazy)(_config__WEBPACK_IMPORTED_MODULE_9__[\"default\"].components.ResultMessage || _imports__WEBPACK_IMPORTED_MODULE_8__.resultMessageImport);\nconst isHandheld = (bowser__WEBPACK_IMPORTED_MODULE_3___default().mobile) || (bowser__WEBPACK_IMPORTED_MODULE_3___default().tablet);\nconst ZERO_WIN_AMOUNT = 0;\nfunction GameResults({\n  // newline\n  round,\n  winnerList,\n  gameResultsConfig,\n  winnerListConfig,\n  dealerName,\n  toggleTips,\n  onTipsClick,\n  widgets,\n  className,\n  tipsStyle,\n  isWinner,\n  jackpotWinner,\n  clientId,\n  setMenu,\n  shouldCelebrate,\n  isCricketWar,\n  totalWinningsAmount,\n  ...props\n}) {\n  var _config$props$WinnerL, _config$props$WinnerL2;\n  const {\n    toggled\n  } = (0,react__WEBPACK_IMPORTED_MODULE_0__.useContext)(_contexts__WEBPACK_IMPORTED_MODULE_14__.BoardToggleContext);\n  const {\n    resultTitle,\n    winLabel,\n    tipLabel,\n    tipTheDealer,\n    jackpotYouWin,\n    jackpotUserWins,\n    totalWinningsLabel\n  } = (0,_hooks__WEBPACK_IMPORTED_MODULE_7__.useIntl)({\n    keys: {\n      resultTitle: \"roulette_ui.round_results_message\",\n      winLabel: \"games_ui.you_win\",\n      tipLabel: \"games_ui.tip\",\n      tipTheDealer: \"general.tip_the_dealer\",\n      jackpotYouWin: \"general.jackpot_you_win\",\n      jackpotUserWins: \"general.jackpot_user_wins\",\n      totalWinningsLabel: \"general.total_winnings\"\n    }\n  });\n  const {\n    winAmountDisplay,\n    jackpotWinAmountDisplay\n  } = (0,_hooks__WEBPACK_IMPORTED_MODULE_7__.useAmount)({\n    values: {\n      winAmountDisplay: {\n        value: round.winAmount || ZERO_WIN_AMOUNT,\n        type: \"bet\"\n      },\n      jackpotWinAmountDisplay: {\n        value: jackpotWinner.winAmount || ZERO_WIN_AMOUNT,\n        type: \"bet\"\n      }\n    }\n  });\n  const {\n    totalWinningsAmountDisplay\n  } = (0,_hooks__WEBPACK_IMPORTED_MODULE_7__.useAmount)({\n    values: {\n      totalWinningsAmountDisplay: {\n        value: totalWinningsAmount,\n        type: \"bet\"\n      }\n    }\n  });\n  const showResultMessage = round.roundStatus === _constants_es__WEBPACK_IMPORTED_MODULE_4__.GAME_RESULT && isWinner;\n  let showWinnerList = false; // on PC, winner list can be disabled from the game\n\n  // if (isHandheld || !props.hideWinnerList) {\n  //   // don't show winner list when there are no winners, or the game engine\n  //   // is not in a correct state\n  //   showWinnerList = winnerList.length > 0 && [GAME_RESULT, PLACE_YOUR_BETS].includes(round.roundStatus);\n  // }\n\n  const showJackpot = !!jackpotWinner.winAmount;\n  const isJackpotWinner = jackpotWinner.ClientId === clientId;\n  const jackpotWinningMessage = isJackpotWinner ? jackpotYouWin : `${jackpotWinner.Nickname} ${jackpotUserWins}`;\n  const handleOnTipsClick = (0,react__WEBPACK_IMPORTED_MODULE_0__.useCallback)(() => {\n    setMenu({\n      widget: {\n        key: \"tips\",\n        label: tipLabel,\n        drawer: true\n      },\n      open: false\n    });\n  }, [tipLabel, setMenu]);\n  const orientation = (0,_hooks__WEBPACK_IMPORTED_MODULE_6__.useOrientationChange)(); // eslint-disable-next-line react-hooks/exhaustive-deps\n\n  const isSmallWindow = (0,react__WEBPACK_IMPORTED_MODULE_0__.useMemo)(() => window.innerHeight < 620, [orientation]);\n  let winnerListHeader;\n  if ((_config$props$WinnerL = _config__WEBPACK_IMPORTED_MODULE_9__[\"default\"].props.WinnerList) !== null && _config$props$WinnerL !== void 0 && _config$props$WinnerL.showHeader) {\n    winnerListHeader = typeof _config__WEBPACK_IMPORTED_MODULE_9__[\"default\"].props.WinnerList.header === \"function\" ? _config__WEBPACK_IMPORTED_MODULE_9__[\"default\"].props.WinnerList.header : {\n      label: totalWinningsLabel,\n      amount: totalWinningsAmountDisplay,\n      ..._config__WEBPACK_IMPORTED_MODULE_9__[\"default\"].props.WinnerList.header\n    };\n  }\n  return /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement(react__WEBPACK_IMPORTED_MODULE_0__.Suspense, {\n    fallback: null\n  }, /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement(_style__WEBPACK_IMPORTED_MODULE_15__.Container, _extends({\n    \"data-e2e\": \"game-results\",\n    topSpaceDesktop: gameResultsConfig.topSpaceDesktop,\n    topSpacePortrait: gameResultsConfig.topSpacePortrait,\n    className: (0,_emotion_css__WEBPACK_IMPORTED_MODULE_1__.cx)({\n      [_style__WEBPACK_IMPORTED_MODULE_15__.mobileContainer]: isHandheld,\n      [_style__WEBPACK_IMPORTED_MODULE_15__.fixedContainer]: toggled || isSmallWindow,\n      [_style__WEBPACK_IMPORTED_MODULE_15__.jackpotContainer]: showJackpot\n    }, className)\n  }, props), showJackpot && /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement(_components__WEBPACK_IMPORTED_MODULE_5__.Jackpot.Winner, {\n    className: (0,_emotion_css__WEBPACK_IMPORTED_MODULE_1__.cx)({\n      [_style__WEBPACK_IMPORTED_MODULE_15__.mobileJackpotClassName]: isHandheld\n    }),\n    isWinner: isJackpotWinner,\n    winningMessage: jackpotWinningMessage,\n    amountDisplay: jackpotWinAmountDisplay\n  }), showResultMessage ? /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement(ResultMessage, _extends({\n    dealerName: dealerName,\n    resultTitle: resultTitle,\n    isWinner: shouldCelebrate,\n    winAmount: {\n      value: winAmountDisplay,\n      label: winLabel,\n      visible: round.winAmount > 0\n    },\n    tipLabel: tipLabel,\n    tipTheDealer: tipTheDealer,\n    tipsEnabled: widgets.tips,\n    toggleTips: toggleTips,\n    onTipsClick: handleOnTipsClick,\n    \"data-e2e\": \"result-message\",\n    tipsStyle: tipsStyle\n  }, _config__WEBPACK_IMPORTED_MODULE_9__[\"default\"].props.ResultMessage, {\n    className: (0,_emotion_css__WEBPACK_IMPORTED_MODULE_1__.cx)({\n      [_style__WEBPACK_IMPORTED_MODULE_15__.jackpotWinClassName]: showJackpot && isHandheld\n    }, _config__WEBPACK_IMPORTED_MODULE_9__[\"default\"].props.ResultMessage && _config__WEBPACK_IMPORTED_MODULE_9__[\"default\"].props.ResultMessage.className)\n  })) : null, showWinnerList ? /*#__PURE__*/react__WEBPACK_IMPORTED_MODULE_0___default().createElement(_components__WEBPACK_IMPORTED_MODULE_5__.WinnerList, _extends({\n    \"data-e2e\": \"result-message-winner-list\"\n  }, winnerListConfig, {\n    style: (_config$props$WinnerL2 = _config__WEBPACK_IMPORTED_MODULE_9__[\"default\"].props.WinnerList) === null || _config$props$WinnerL2 === void 0 ? void 0 : _config$props$WinnerL2.style,\n    list: winnerList,\n    header: winnerListHeader,\n    className: (0,_emotion_css__WEBPACK_IMPORTED_MODULE_1__.cx)({\n      [_style__WEBPACK_IMPORTED_MODULE_15__.mobileWinnerListClassName]: isHandheld\n    })\n  })) : null));\n}\nconst {\n  menu\n} = _store_actions_menu__WEBPACK_IMPORTED_MODULE_13__[\"default\"];\nconst {\n  tips\n} = _store_actions_tips__WEBPACK_IMPORTED_MODULE_12__[\"default\"];\nconst mapDispatchToProps = {\n  toggleTips: tips.toggleRequest,\n  setMenu: menu.set\n};\nconst mapStateToProps = (0,ramda__WEBPACK_IMPORTED_MODULE_16__[\"default\"])({\n  round: _store_selectors__WEBPACK_IMPORTED_MODULE_10__.roundSelector,\n  winnerList: _store_selectors__WEBPACK_IMPORTED_MODULE_10__.sortedWinnerListSelector,\n  gameResultsConfig: _store_selectors__WEBPACK_IMPORTED_MODULE_10__.gameResultsConfigSelector,\n  winnerListConfig: _store_selectors__WEBPACK_IMPORTED_MODULE_10__.winnerListPropsSelector,\n  widgets: _store_selectors__WEBPACK_IMPORTED_MODULE_10__.widgetsSelector,\n  dealerName: _store_selectors__WEBPACK_IMPORTED_MODULE_10__.dealerNameSelector,\n  tipsStyle: _store_selectors__WEBPACK_IMPORTED_MODULE_10__.tipButtonStyleSelector,\n  isWinner: _store_selectors__WEBPACK_IMPORTED_MODULE_10__.winnerSelector,\n  shouldCelebrate: _store_selectors__WEBPACK_IMPORTED_MODULE_10__.showCelebrationAnimationSelector,\n  jackpotWinner: _store_selectors_jackpot__WEBPACK_IMPORTED_MODULE_11__.jackpotWinnerSelector,\n  clientId: _store_selectors__WEBPACK_IMPORTED_MODULE_10__.clientIdSelector,\n  isCricketWar: _store_selectors__WEBPACK_IMPORTED_MODULE_10__.isCricketWarSelector,\n  totalWinningsAmount: _store_selectors__WEBPACK_IMPORTED_MODULE_10__.totalWinningsAmountSelector\n});\nGameResults.defaultProps = {\n  isWinner: true,\n  jackpotWinner: {},\n  setMenu: () => {}\n};\nGameResults.propTypes = {\n  round: prop_types__WEBPACK_IMPORTED_MODULE_17__.object,\n  winnerList: prop_types__WEBPACK_IMPORTED_MODULE_17__.array,\n  hideWinnerList: prop_types__WEBPACK_IMPORTED_MODULE_17__.bool,\n  dealerName: prop_types__WEBPACK_IMPORTED_MODULE_17__.string,\n  toggleTips: prop_types__WEBPACK_IMPORTED_MODULE_17__.func,\n  onTipsClick: prop_types__WEBPACK_IMPORTED_MODULE_17__.func,\n  widgets: prop_types__WEBPACK_IMPORTED_MODULE_17__.object,\n  tipsStyle: prop_types__WEBPACK_IMPORTED_MODULE_17__.object,\n  isWinner: prop_types__WEBPACK_IMPORTED_MODULE_17__.bool,\n  shouldCelebrate: prop_types__WEBPACK_IMPORTED_MODULE_17__.bool,\n  jackpotWinner: prop_types__WEBPACK_IMPORTED_MODULE_17__.object,\n  clientId: prop_types__WEBPACK_IMPORTED_MODULE_17__.string,\n  setMenu: prop_types__WEBPACK_IMPORTED_MODULE_17__.func,\n  isCricketWar: prop_types__WEBPACK_IMPORTED_MODULE_17__.bool,\n  gameResultsConfig: prop_types__WEBPACK_IMPORTED_MODULE_17__.object,\n  winnerListConfig: prop_types__WEBPACK_IMPORTED_MODULE_17__.object,\n  totalWinningsAmount: prop_types__WEBPACK_IMPORTED_MODULE_17__.number\n};\n/* harmony default export */ const __WEBPACK_DEFAULT_EXPORT__ = ((0,react_redux__WEBPACK_IMPORTED_MODULE_2__.connect)(mapStateToProps, mapDispatchToProps)(GameResults));\n\n//# sourceURL=webpack://tykhe-electron-apps-roulette/./src/renderer/roulette/vtopper/tykhe/bootstrap/views/GameResults/index.js?");

/***/ })

}]);