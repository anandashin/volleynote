package com.anandashin.volleynote.fivb

import com.anandashin.volleynote.common.BusinessException
import com.anandashin.volleynote.common.ErrorCode

class TournamentNotFoundException : BusinessException(ErrorCode.TOURNAMENT_NOT_FOUND)

class MatchNotFoundException : BusinessException(ErrorCode.MATCH_NOT_FOUND)
