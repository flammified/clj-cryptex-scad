(ns cryptex-verjaardag.core
  (:use [scad-clj.scad])
  (:use [scad-clj.model]))

(def ring-width 30)
(def slot-width 10)
(def alphabet (vec (map char (concat (range 65 91)))))
(println alphabet)

(defn main-part [length]
  (let [outer (with-fn 50
                (->> (cylinder 60 length)
                     (rotate (/ Math/PI 2) [0 1 0])
                     (translate [(+ 20 (/ length 2)) 0 -70])))
        inner (with-fn 50
                (->> (cylinder 45 (+ length 10))
                     (rotate (/ Math/PI 2) [0 1 0])
                     (translate [(+ 20 (/ length 2)) 0 -70])))
        slot  (->> (cube (+ 25 length) slot-width 40)
                   (translate [(+ 30 (/ length 2)) 0 -20]))
        ring (difference outer inner)
        intersection-to-remove (intersection ring slot)]
    (difference ring slot)))

(defn emboss-letter [letter angle radius ring]
  (println letter)
  (difference
    ring
    (->> (text letter)
         (extrude-linear {:height 5})
         (rotate angle [1 0 0])
         (translate [10 (- 0 (* radius (Math/sin (+ angle 0.05)))) (* radius (Math/cos (+ angle 0.05)))]))))


(defn add-slots [ring offset]
  (difference
    (reduce
      (fn [result index]
        (let [angle (* index (/ (* 2 Math/PI) 26))
              radius 65]
          (difference
            (emboss-letter (get (vec (take 52 (cycle alphabet))) (+ index offset)) angle 78 result)
            (->> (cube 40 10 20)
                 (rotate angle [1 0 0])
                 (translate [5 (- 0 (* radius (Math/sin angle))) (* radius (Math/cos angle))])))))
      ring
      (range 0 26))
    (->> (cube 50 7.5 20)
         (translate [10 0 65]))))


(defn movable-ring [width offset]
  (let [outer (with-fn 100
                 (->> (cylinder 80 width)
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

      (->> (cube 20 90 180)
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
          (->> (cylinder 10 (+ lock-length stick-length))
               (rotate (/ Math/PI 2) [0 1 0])
               (translate [(- (+ 20 stick-length) (/ (+ lock-length stick-length) 2)) 0 120]))))
      amount-of-rings)))

(defn stop-end [width]
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
                 (->> (cylinder 55 10)
                      (rotate (/ Math/PI 2) [0 1 0])
                      (translate [0 0 -70]))
                 (->> (cylinder 50 11)
                      (rotate (/ Math/PI 2) [0 1 0])
                      (translate [0 0 -70])))
               (->> (cube (+ 50 length) (+ slot-width 5) 50)
                    (translate [(/ length 2) 0 -20])))
             (translate [5 0 0])))))


(spit "cryptex.scad"
  (let [amount-of-rings 7
        stop-end (stop-end (/ ring-width 2))]
    (write-scad
                (difference (plate) (translate [0 0 120] (rotate (/ Math/PI 2) [0 1 0] (cylinder 15 100))))
                (difference
                  (main-part)
                  (translate [(+ 5 (* amount-of-rings (+ ring-width 1))) 0 0 stop-end]))
                (main-part (* amount-of-rings (+ ring-width 1)))
                (translate [600 0 0] (stick (+ 5 (* amount-of-rings (+ ring-width 1))) amount-of-rings))
                (rings amount-of-rings "abraham")
                (translate [(+ 500 (* (+ 0 amount-of-rings) (+ ring-width 1))) 0 0]) stop-end)))
