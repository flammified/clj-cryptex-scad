(ns cryptex-verjaardag.core
  (:use [scad-clj.scad])
  (:use [scad-clj.model]))

(def ring-width 30)
(def slot-width 10)
(def alphabet (vec (map char (concat (range 65 91)))))

;; TODO remove magic numbers
(defn main-part [length]
  (let [main-part-radius 60
        main-part-thickness
        outer (with-fn 50
                (->> (cylinder main-part-radius length)
                     (rotate (/ Math/PI 2) [0 1 0])
                     (translate [(+ (- main-part-thickness 1) (/ length 2)) 0 -70])))
        inner (with-fn 50
                (->> (cylinder (- main-part-radius 15) (+ length 10)) ;length is + 10 purely for stability
                     (rotate (/ Math/PI 2) [0 1 0])
                     (translate [(+ main-part-thickness (/ length 2)) 0 -70]))) ; + 20 for the plate
        slot  (->> (cube (+ 25 length) slot-width 40)
                   (translate [(+ 30 (/ length 2)) 0 -10]))
        ring (difference outer inner)
        intersection-to-remove (intersection ring slot)]
    ; (union
    (difference ring slot)))
      ; (->> triangle
      ;      (rotate (/ Math/PI 2) [0 0 1])
      ;      (rotate (* (/ Math/PI 2) 3) [0 1 0])
      ;      (translate [20 -10 25])))))


(defn emboss-letter [letter angle radius ring]
  (union
    ring
    (->> (text letter :size 15)
         (extrude-linear {:height 4})
         (rotate angle [1 0 0])

         ;offset compensates for bounding box of the letter (+ angle <offset>)
         ;TODO calculate from bounding box for more configurable cryptex
         (translate [7.5 (- 0 (* radius (Math/sin (+ angle 0.08)))) (* radius (Math/cos (+ angle 0.08)))]))))


(defn add-slots [ring offset]
  (difference
    (reduce
      (fn [result index]
        (let [angle (* index (/ (* 2 Math/PI) 26))
              radius 65]
          (difference
            (emboss-letter (get (vec (take 52 (cycle alphabet))) (+ index offset)) angle 85 result)
            (->> (cube 40 10 20)
                 (rotate angle [1 0 0])
                 (translate [5 (- 0 (* radius (Math/sin angle))) (* radius (Math/cos angle))])))))
      ring
      (range 0 26))
    (->> (cube 50 7.5 20)
         (translate [10 0 65]))))


(defn movable-ring [width offset]
  (let [outer (with-fn 100
                 (->> (cylinder 85 width)
                      (rotate (/ Math/PI 2) [0 1 0])
                      (translate [(/ width 2) 0 0])))
        hole (with-fn 100
                 (->> (cylinder 62 (+ width 10))
                      (rotate (/ Math/PI 2) [0 1 0])
                      (translate [(/ width 2) 0 0])))
        inner (with-fn 100
                 (->> (cylinder 75 (+ 0.1 (/ width 2)))
                      (rotate (/ Math/PI 2) [0 1 0])
                      (translate [7.50 0 0])))]
    (add-slots (difference (difference outer hole) inner) offset)))

(defn plate []
  (union
    (with-fn 50
      (->> (cylinder 90 20)
           (rotate (/ Math/PI 2) [0 1 0])
           (translate [10 0 -70]))

      (->> (cube 20 40 180)
           (translate [10 0 65])))))

(defn letter-to-offset [letter]
  (println letter)
  (- (int letter) 97))

(defn rings [amount word]
  (let [characters (vec (seq (char-array word)))]
    (map #(->> (movable-ring ring-width (letter-to-offset (get characters %))) (translate [(+ 21 (* % (+ ring-width 1))) 0 -70])) (range 0 amount))))

(defn add-pins [stick amount-of-rings]
  (reduce
    (fn [result index]
      (union
        (->> (cube 12 6 30)
            (translate [(+ 27 (* index (+ ring-width 1))) 0 -12]))
        result))
    stick
    (range 0 amount-of-rings)))

(defn stick [stick-length amount-of-rings]
  (let [lock-length 20]
    (add-pins
      (union
        (with-fn 50
          (->> (cylinder 43 stick-length)
               (rotate (/ Math/PI 2) [0 1 0])
               (translate [(+ 20 (/ stick-length 2)) 0 -70]))
          (->> (plate)
               (translate [(+ stick-length 20) 0 0]))
          (->> (cylinder 5 (+ lock-length stick-length))
               (rotate (/ Math/PI 2) [0 1 0])
               (translate [(- (+ 20 stick-length) (/ (+ lock-length stick-length) 2)) 0 120]))))
      amount-of-rings)))

;I dont even know anymore
(defn stop-end [width middle-height middle-length]
    (let [length 20
          outer (with-fn 50
                  (->> (cylinder 80 length)
                       (rotate (/ Math/PI 2) [0 1 0])
                       (translate [(+ 10 (/ length 2)) 0 -70])))
             inner (with-fn 50
                     (->> (cylinder 45 (+ length 10))
                          (rotate (/ Math/PI 2) [0 1 0])
                          (translate [(+ 10 (/ length 2)) 0 -70])))
             slot  (->> (cube (+ 25 length) slot-width 50)
                        (translate [(/ length 2) 0 -20]))
             ring (difference outer inner)
             intersection-to-remove (intersection ring slot)]
      (union
        (difference ring slot)
        (->> (difference
               (difference
                 (->> (cylinder (+ 52.5 (/ middle-height 2)) middle-length)
                      (rotate (/ Math/PI 2) [0 1 0])
                      (translate [0 0 -70]))
                 (->> (cylinder (- 52.5 (/ middle-height 2)) (+ middle-length 1))
                      (rotate (/ Math/PI 2) [0 1 0])
                      (translate [0 0 -70])))
               (->> (cube (+ 50 length) (+ slot-width 5) 50)
                    (translate [(/ length 2) 0 -20])))
             (translate [5 0 0])))))



(defn cryptex [word]
  (let [amount-of-rings (count word)
        stop-end-difference (stop-end (/ ring-width 2) 6 14)
        hole-stick 8]
      (translate [-400 0 0]
        (union
          (difference (plate) (translate [0 0 120] (rotate (/ Math/PI 2) [0 1 0] (cylinder hole-stick 100))))
          (difference
            (main-part (+ 2 (* amount-of-rings (+ ring-width 1))))
            (translate [ (+ 20 (* amount-of-rings ring-width)) 0 0] stop-end-difference))
          (translate [ (+ 50 18 (* amount-of-rings ring-width)) 0 0] (stop-end (/ ring-width 2) 5 11))
          (translate [500 0 0] (stick (+ 15 20 (* amount-of-rings (+ ring-width 1))) amount-of-rings))
          (rings amount-of-rings word)))))

(spit "output/cryptex.scad"
  (write-scad (cryptex "abraham")))
