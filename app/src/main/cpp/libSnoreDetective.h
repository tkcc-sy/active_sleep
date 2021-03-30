//
// Created by y-kozawa on 2020/09/09.
//

#ifndef LIBSNOREDETECTIVE_LIBSNOREDETECTIVE_H
#define LIBSNOREDETECTIVE_LIBSNOREDETECTIVE_H

#include <stdio.h>

/* extern "C" */
#ifdef __cplusplus
extern "C"
{
#endif

/* ライブラリ初期化 */
extern int64_t SDL_SnoreInitialize();

/* いびき解析開始 */
extern int64_t SDL_SnoreAnalysis(const char* fileNamePath, const char* outPath,
                            int snoTime, int snoTh, int outInterval, int outSnoreFileTime, int outSnoreFileCount);

/* いびき解析中止 */
extern int64_t SDL_SnoreAnalysisCancel();

/* エラーコード取得 */
extern int64_t SDL_GetErrorCode();

#ifdef __cplusplus
}
#endif

#endif //LIBSNOREDETECTIVE_LIBSNOREDETECTIVE_H
