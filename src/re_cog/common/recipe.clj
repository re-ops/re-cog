(ns re-cog.common.recipe)

(defmacro require-recipe []
  `(do
     (require
      '[re-cog.recipes.access]
      '[re-cog.common.defs]
      '[re-cog.common.functions]
      '[re-cog.common.constants])
     (re-cog.common.defs/require-defs)
     (re-cog.common.constants/require-constants)
     (re-cog.common.functions/require-functions)
     (re-cog.common.functions/require-resources)))
