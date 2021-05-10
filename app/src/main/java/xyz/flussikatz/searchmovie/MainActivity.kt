package xyz.flussikatz.searchmovie

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var filmsAdapter: FilmListRecyclerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val filmDataBase = listOf(
            Film("Властелин колец: Братство Кольца", R.drawable.lotr1, "Тихая деревня, где живут хоббиты. Придя на 111-й день рождения к своему старому другу Бильбо Бэггинсу, волшебник Гэндальф начинает вести разговор о кольце, которое Бильбо нашел много лет назад. Это кольцо принадлежало когда-то темному властителю Средиземья Саурону, и оно дает большую власть своему обладателю. Теперь Саурон хочет вернуть себе власть над Средиземьем. Бильбо отдает Кольцо племяннику Фродо, чтобы тот отнёс его к Роковой Горе и уничтожил." ),
            Film("Властелин колец: Две крепости",R.drawable.lotr2,"Братство распалось, но Кольцо Всевластья должно быть уничтожено. Фродо и Сэм вынуждены доверится Голлуму, который взялся провести их к вратам Мордора. Громадная армия Сарумана приближается: члены братства и их союзники готовы принять бой. Битва за Средиземье продолжается."),
            Film("Властелин колец: Возвращение короля",R.drawable.lotr,"Повелитель сил тьмы Саурон направляет свою бесчисленную армию под стены Минас-Тирита, крепости Последней Надежды. Он предвкушает близкую победу, но именно это мешает ему заметить две крохотные фигурки — хоббитов, приближающихся к Роковой Горе, где им предстоит уничтожить Кольцо Всевластья."),
            Film("Список Шиндлера",R.drawable.spisok,"Фильм рассказывает реальную историю загадочного Оскара Шиндлера, члена нацистской партии, преуспевающего фабриканта, спасшего во время Второй мировой войны почти 1200 евреев."),
            Film("Интерстеллар",R.drawable.interstellar,"Когда засуха, пыльные бури и вымирание растений приводят человечество к продовольственному кризису, коллектив исследователей и учёных отправляется сквозь червоточину (которая предположительно соединяет области пространства-времени через большое расстояние) в путешествие, чтобы превзойти прежние ограничения для космических путешествий человека и найти планету с подходящими для человечества условиями."),
            Film("Зеленая миля",R.drawable.green_mile,"Пол Эджкомб - начальник блока смертников в тюрьме «Холодная гора», каждый из узников которого однажды проходит «зеленую милю» по пути к месту казни. Пол повидал много заключённых и надзирателей за время работы. Однако гигант Джон Коффи, обвинённый в страшном преступлении, стал одним из самых необычных обитателей блока."),
            Film("Побег из Шоушенка",R.drawable.escape,"Бухгалтер Энди Дюфрейн обвинён в убийстве собственной жены и её любовника. Оказавшись в тюрьме под названием Шоушенк, он сталкивается с жестокостью и беззаконием, царящими по обе стороны решётки. Каждый, кто попадает в эти стены, становится их рабом до конца жизни. Но Энди, обладающий живым умом и доброй душой, находит подход как к заключённым, так и к охранникам, добиваясь их особого к себе расположения."),
            Film("Форрест Гамп",R.drawable.forest,"От лица главного героя Форреста Гампа, слабоумного безобидного человека с благородным и открытым сердцем, рассказывается история его необыкновенной жизни.Фантастическим образом превращается он в известного футболиста, героя войны, преуспевающего бизнесмена. Он становится миллиардером, но остается таким же бесхитростным, глупым и добрым. Форреста ждет постоянный успех во всем, а он любит девочку, с которой дружил в детстве, но взаимность приходит слишком поздно.")
        )

        materialToolbar.setNavigationOnClickListener {
        }

        materialToolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.settings -> {Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        bottomToolBar.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.search -> {Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.history -> {Toast.makeText(this, "History", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.marked -> {Toast.makeText(this, "Marked", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }


        val animatorSet = AnimatorSet()
        val posterAnim1 = ObjectAnimator.ofFloat(posters, View.SCALE_X, 0f, 1F)
        val posterAnim2 = ObjectAnimator.ofFloat(posters, View.SCALE_Y, 0f, 1F)
        val animationUpdateListener = object: Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                text1.alpha = 1f
                text1.startAnimation(AnimationUtils.loadAnimation(this@MainActivity, R.anim.text_anim))
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }

        }
//        val textAnim = ObjectAnimator.ofFloat(text1, View.ALPHA, 0f, 1f)

        animatorSet.playTogether(posterAnim1,posterAnim2)
        animatorSet.interpolator = OvershootInterpolator()
        animatorSet.startDelay = 500
        animatorSet.addListener(animationUpdateListener)
        animatorSet.setDuration(1000).start()

        val film_recycler = findViewById<RecyclerView>(R.id.film_recycler)
        film_recycler.apply {
            filmsAdapter = FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener{
                override fun click(film: Film) {
                    val bundle = Bundle()
                    bundle.putParcelable("film", film)
                    val intent = Intent(this@MainActivity, DetailsActivity::class.java)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
            })
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            val decorator = TopSpasingItemDecoration(5)
            addItemDecoration(decorator)

        }
        filmsAdapter.addItems(filmDataBase)

    }
}