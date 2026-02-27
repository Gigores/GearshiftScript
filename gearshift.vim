if exists("b:current_syntax")
    finish
endif

syntax case match

syn keyword gearKeyword     function let const end if then else while for in do return struct is static new
syn keyword gearConditional if else
syn keyword gearRepeat      for while
syn keyword gearOperator    and or not step
syn keyword gearSpecial     this
syn keyword gearSpecial     print println input typeOf ensureType
syn keyword gearSpecial     __init __add __sub __mul __div __eq __gt __ls __pow

syn keyword gearType        String Number Boolean
syn keyword gearBoolean     true false null
syn keyword gearTodo        contained TODO FIXME XXX NOTE HACK OPTIMIZE
syn match   gearComment     "#.*$"   contains=gearTodo@Spell
syn match   gearDelimiter   "[(){}\[\];,:.]"

syn region  gearString      start=+"+  end=+"+  skip=+\\\\\|\\"+  contains=mylangEscape
syn region  gearString      start=+'+  end=+'+  skip=+\\\\\|\\'+  contains=mylangEscape
syn match   gearEscape      contained  +\\[nrtbf0"\\']+

syn match   gearNumber      "\v-?(\d+(\.\d*)?|\.\d+)"

syn match   gearFunction    "\<\zs\w\+\ze\s*("
syn match   gearStruct      "\<[A-Z][a-zA-Z0-9_]*\>"
syn match   gearConstant    "\<[A-Z][A-Z0-9_]*\>"
syn match   gearIdentifier  "\<\l[a-zA-Z0-9_]*\>"


hi link gearKeyword     Keyword
hi link gearConditional Conditional
hi link gearRepeat      Repeat
hi link gearOperator    Operator
hi link gearSpecial     Special
hi link gearType        Type
hi link gearBoolean     Boolean
hi link gearTodo        Todo
hi link gearComment     Comment
hi link gearDelimiter   Delimiter
hi link gearString      String
hi link gearEscape      SpecialChar
hi link gearNumber      Number
hi link gearFunction    Function
hi link gearStruct      TypeDef
hi link gearConstant    Constant
hi link gearIdentifier  Identifier

let b:current_syntax = "gearshift"
