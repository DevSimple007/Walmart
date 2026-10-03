<template>
  <div class="all-departments-page">
    <div class="page-container">
      <h1>{{ $t('allDepartments.title') }}</h1>
      <div class="departments-grid">
        <div class="dept-column" v-for="(col, ci) in departmentColumns" :key="ci">
          <div class="dept-section" v-for="dept in col" :key="dept.key">
            <h3><router-link :to="dept.link">{{ $t('dept.' + dept.key) }}</router-link></h3>
            <ul>
              <li v-for="(sub, si) in dept.subs" :key="si"><router-link :to="dept.link">{{ $t('adp.' + dept.key + '_' + si) }}</router-link></li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
const departmentColumns = [
  [
    { key: 'summer-deals', link: '/shop/deals', subs: ['s0', 's1', 's2', 's3', 's4'] },
    { key: '4th-july', link: '/shop/deals', subs: ['j0', 'j1', 'j2', 'j3'] },
    { key: 'grocery', link: '/shop/deals/food', subs: ['g0', 'g1', 'g2', 'g3', 'g4', 'g5'] },
    { key: 'clothing', link: '/shop/deals/clothing-and-accessories', subs: ['c0', 'c1', 'c2', 'c3', 'c4', 'c5', 'c6'] }
  ],
  [
    { key: 'baby', link: '/shop/deals/baby', subs: ['b0', 'b1', 'b2', 'b3', 'b4'] },
    { key: 'pharmacy', link: '/shop/deals/pharmacy', subs: ['p0', 'p1', 'p2', 'p3', 'p4'] },
    { key: 'home', link: '/shop/deals/all-home', subs: ['h0', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6'] },
    { key: 'electronics', link: '/shop/deals/electronics', subs: ['e0', 'e1', 'e2', 'e3', 'e4', 'e5', 'e6'] }
  ],
  [
    { key: 'gaming', link: '/shop/deals', subs: ['ga0', 'ga1', 'ga2', 'ga3', 'ga4'] },
    { key: 'auto', link: '/shop/deals', subs: ['a0', 'a1', 'a2', 'a3', 'a4'] },
    { key: 'personal-care', link: '/shop/deals/beauty-and-personal-care', subs: ['pc0', 'pc1', 'pc2', 'pc3', 'pc4'] },
    { key: 'beauty', link: '/shop/deals/beauty-and-personal-care', subs: ['be0', 'be1', 'be2', 'be3', 'be4'] }
  ],
  [
    { key: 'toys', link: '/shop/deals/toys', subs: ['t0', 't1', 't2', 't3', 't4', 't5'] },
    { key: 'household', link: '/shop/deals/household-essentials', subs: ['ho0', 'ho1', 'ho2', 'ho3', 'ho4'] },
    { key: 'pets', link: '/shop/deals', subs: ['pe0', 'pe1', 'pe2', 'pe3', 'pe4'] },
    { key: 'school', link: '/shop/deals/school-office-art', subs: ['sc0', 'sc1', 'sc2', 'sc3', 'sc4'] }
  ],
  [
    { key: 'sports', link: '/shop/deals', subs: ['sp0', 'sp1', 'sp2', 'sp3', 'sp4'] },
    { key: 'collectibles', link: '/shop/deals', subs: ['co0', 'co1', 'co2', 'co3', 'co4'] },
    { key: 'party', link: '/shop/deals', subs: ['pa0', 'pa1', 'pa2', 'pa3', 'pa4'] },
    { key: 'financial', link: '/shop/deals', subs: ['f0', 'f1', 'f2', 'f3'] }
  ]
]
</script>

<style lang="scss" scoped>
$walmart-blue: #0071dc; $walmart-gray-100: #f5f5f5; $walmart-gray-600: #2e2f32; $walmart-white: #fff;

.all-departments-page { background: $walmart-gray-100; min-height: 100vh; padding: 24px 0; }
.page-container { max-width: 1440px; margin: 0 auto; padding: 0 24px; }
h1 { font-size: 28px; font-weight: 700; color: $walmart-gray-600; margin-bottom: 24px; }
.departments-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 24px; }
.dept-section { margin-bottom: 24px;
  h3 { font-size: 16px; font-weight: 700; margin-bottom: 8px;
    a { color: $walmart-gray-600; text-decoration: none; &:hover { color: $walmart-blue; } }
  }
  ul { list-style: none; padding: 0;
    li { margin-bottom: 6px;
      a { font-size: 13px; color: $walmart-blue; text-decoration: none; &:hover { text-decoration: underline; } }
    }
  }
}
</style>
