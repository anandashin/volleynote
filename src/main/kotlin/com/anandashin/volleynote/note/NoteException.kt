package com.anandashin.volleynote.note

import com.anandashin.volleynote.common.BusinessException
import com.anandashin.volleynote.common.ErrorCode

class NoteNotFoundException : BusinessException(ErrorCode.NOTE_NOT_FOUND)

class NoteAccessDeniedException : BusinessException(ErrorCode.NOTE_ACCESS_DENIED)
