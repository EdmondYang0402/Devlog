import { onMounted, onUnmounted } from 'vue'

export function useStarryBackground(canvasId, sceneId) {
    let animId = null
    let canvas, ctx, stars, petals, particles, resizeHandler, clickHandler

    const init = () => {
        canvas = document.getElementById(canvasId)
        const scene = document.getElementById(sceneId)
        if (!canvas || !scene) return
        ctx = canvas.getContext('2d')

        resizeHandler = () => {
            canvas.width = scene.offsetWidth
            canvas.height = scene.offsetHeight
        }
        resizeHandler()
        window.addEventListener('resize', resizeHandler)

        stars = Array.from({ length: 120 }, () => ({
            x: Math.random() * canvas.width,
            y: Math.random() * canvas.height,
            r: Math.random() * 1.2 + 0.3,
            speed: Math.random() * 0.008 + 0.003,
            phase: Math.random() * Math.PI * 2,
            alpha: Math.random()
        }))

        petals = Array.from({ length: 20 }, () => ({
            x: Math.random() * canvas.width,
            y: Math.random() * canvas.height - canvas.height,
            r: Math.random() * 4 + 3,
            vx: (Math.random() - 0.5) * 0.6,
            vy: Math.random() * 0.8 + 0.3,
            angle: Math.random() * Math.PI * 2,
            spin: (Math.random() - 0.5) * 0.04,
            phase: Math.random() * Math.PI * 2,
            opacity: Math.random() * 0.5 + 0.3
        }))

        particles = []

        clickHandler = (e) => {
            const tag = e.target.tagName
            if (['INPUT', 'BUTTON', 'A', 'LABEL', 'TEXTAREA'].includes(tag)) return
            const rect = scene.getBoundingClientRect()
            const mx = e.clientX - rect.left
            const my = e.clientY - rect.top
            for (let i = 0; i < 12; i++) {
                const angle = (Math.PI * 2 / 12) * i
                particles.push({
                    x: mx, y: my,
                    vx: Math.cos(angle) * (Math.random() * 2 + 1),
                    vy: Math.sin(angle) * (Math.random() * 2 + 1),
                    life: 1,
                    r: Math.random() * 3 + 1,
                    pink: Math.random() > 0.5
                })
            }
        }
        scene.addEventListener('click', clickHandler)

        let t = 0
        const animate = () => {
            ctx.clearRect(0, 0, canvas.width, canvas.height)
            t += 0.016

            stars.forEach(s => {
                const alpha = 0.4 + 0.5 * Math.sin(t * s.speed * 60 + s.phase)
                ctx.beginPath()
                ctx.arc(s.x, s.y, s.r, 0, Math.PI * 2)
                ctx.fillStyle = `rgba(255,255,255,${alpha * s.alpha + 0.1})`
                ctx.fill()
            })

            petals.forEach(p => {
                p.x += p.vx + Math.sin(t * 0.5 + p.phase) * 0.3
                p.y += p.vy
                p.angle += p.spin
                if (p.y > canvas.height + 20) {
                    p.y = -20
                    p.x = Math.random() * canvas.width
                }
                ctx.save()
                ctx.translate(p.x, p.y)
                ctx.rotate(p.angle)
                ctx.beginPath()
                ctx.ellipse(0, 0, p.r, p.r * 1.6, 0, 0, Math.PI * 2)
                ctx.fillStyle = `rgba(255,182,193,${p.opacity})`
                ctx.fill()
                ctx.restore()
            })

            for (let i = particles.length - 1; i >= 0; i--) {
                const p = particles[i]
                p.x += p.vx
                p.y += p.vy
                p.vy += 0.05
                p.life -= 0.025
                if (p.life <= 0) { particles.splice(i, 1); continue }
                ctx.beginPath()
                ctx.arc(p.x, p.y, p.r * p.life, 0, Math.PI * 2)
                ctx.fillStyle = p.pink
                    ? `rgba(255,182,193,${p.life})`
                    : `rgba(180,160,220,${p.life})`
                ctx.fill()
            }

            animId = requestAnimationFrame(animate)
        }
        animate()
    }

    const cleanup = () => {
        if (animId) cancelAnimationFrame(animId)
        if (resizeHandler) window.removeEventListener('resize', resizeHandler)
        const scene = document.getElementById(sceneId)
        if (scene && clickHandler) scene.removeEventListener('click', clickHandler)
    }

    onMounted(init)
    onUnmounted(cleanup)
}