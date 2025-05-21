(ns remolino 
  (:require
   [clojure.walk :refer [postwalk]]))

(defn apply-theme
  "Walks the hiccup structure and, for each element, looks for
   classes in the component-key or the :library key of the theme
   (which is meant to provide global styles), and applies them 
   to the element."
  ([hiccup theme] (apply-theme hiccup theme (first hiccup)))
  ([hiccup theme component-key]
   (let [component-theme (get theme component-key)
         library-theme (get theme :library)
         result (postwalk
                 (fn [form]
                   (if (and (vector? form) (not (map-entry? form)) (keyword? (first form)))
                     (let [tag (first form)
                           theme-classes (get component-theme tag)
                           library-classes (get library-theme tag)
                           new-classes (into [] (concat theme-classes library-classes))
                           has-classes (not= 0 (count new-classes))]
                       (if has-classes
                         (if (map? (second form))
                           (update form 1 update :class concat new-classes)
                           (into [tag {:class new-classes}] (rest form)))
                         form))
                     form)) ; - else - not a vector starting with a keyword
                 hiccup)] ; - else - no theme for component
     result)))
